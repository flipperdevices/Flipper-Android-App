package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.ForbiddenFrequencyException
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SubGhzViewModel @Inject constructor(
    synchronizationApi: SynchronizationApi,
    closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: Application,
    private val settings: DataStore<Settings>,
    fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : EmulateViewModel(
    synchronizationApi,
    closeEmulateAppTaskHolder,
    application,
    settings,
    fFeatureProvider,
    fDeviceOrchestrator
) {
    override val TAG = "SubGhzViewModel"

    override suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        config: EmulateConfig
    ) {
        calculateTimeoutAndStartEmulate(
            scope = scope,
            config = config,
            oneTimePress = false
        )
    }

    fun onSinglePress(config: EmulateConfig) {
        info { "#onSinglePress" }
        vibrator?.vibrateCompat(
            VIBRATOR_TIME,
            runBlocking { settings.data.first().disabled_vibration }
        )
        if (config.keyType != FlipperKeyType.SUB_GHZ) {
            return
        }

        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled,
                is EmulateButtonState.Loading -> it

                is EmulateButtonState.Inactive -> EmulateButtonState.Active(
                    progress = EmulateProgress.Infinite,
                    config = config
                )

                is EmulateButtonState.Active -> {
                    super.onStopEmulate(force = true)
                    return
                }
            }
        }

        viewModelScope.launch {
            startEmulateInternal(this, config)
        }
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        config: EmulateConfig,
    ) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        calculateTimeoutAndStartEmulate(scope, config, oneTimePress = true)
        emulateHelper.stopEmulate(viewModelScope)
    }

    private suspend fun calculateTimeoutAndStartEmulate(
        scope: CoroutineScope,
        config: EmulateConfig,
        oneTimePress: Boolean
    ) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        val timeout = config.minEmulateTime
        val appStarted: Boolean?

        try {
            appStarted = emulateHelper.startEmulate(
                scope,
                config
            )
            if (appStarted && timeout != null) {
                if (oneTimePress) {
                    emulateButtonStateFlow.emit(
                        EmulateButtonState.Active(
                            progress = EmulateProgress.GrowingAndStop(timeout),
                            config = config
                        )
                    )
                } else {
                    emulateButtonStateFlow.emit(
                        EmulateButtonState.Active(
                            progress = EmulateProgress.Growing(timeout),
                            config = config
                        )
                    )
                }
            }
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return
        } catch (fatal: Throwable) {
            error(fatal) { "Handle fatal exception on emulate subghz" }
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return
        }

        if (!appStarted) {
            info { "Failed start app without crash" }
            emulateHelper.stopEmulateForce()
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
        }
    }
}
