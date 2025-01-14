package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.EmulateHelper
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
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class InfraredViewModel @Inject constructor(
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
    override val TAG = "InfraredViewModel"

    override suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        config: EmulateConfig
    ) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        val appStarted = calculateTimeoutAndStartEmulate(
            scope = scope,
            config = config,
            oneTimePress = false,
            emulateHelper
        )

        if (!appStarted) {
            emulateHelper.stopEmulateForce()
        }
    }

    fun onSinglePress(config: EmulateConfig) {
        info { "#onSinglePress $config" }
        vibrator?.vibrateCompat(
            VIBRATOR_TIME,
            runBlocking { settings.data.first().disabled_vibration }
        )
        if (config.keyType != FlipperKeyType.INFRARED) {
            return
        }

        viewModelScope.launch {
            processSinglePress(this, config)
        }
    }

    private suspend fun processSinglePress(
        scope: CoroutineScope,
        config: EmulateConfig
    ) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        val activeState = EmulateButtonState.Active(EmulateProgress.Infinite, config)
        emulateButtonStateFlow.update { buttonState ->
            when (buttonState) {
                is EmulateButtonState.Disabled,
                is EmulateButtonState.Loading -> buttonState

                is EmulateButtonState.Inactive -> activeState
                is EmulateButtonState.Active -> {
                    emulateHelper.stopEmulateForce()
                    // If press same button, then stop emulate and do nothing
                    if (buttonState.config == config) return

                    // Wait for APP_CLOSED from Flipper and update to new active state
                    emulateButtonStateFlow
                        .filter { waitedButtonState ->
                            waitedButtonState is EmulateButtonState.Inactive
                        }
                        .first()

                    return@update activeState
                }
            }
        }
        startEmulateInternal(scope, config)
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
        calculateTimeoutAndStartEmulate(scope, config, oneTimePress = true, emulateHelper)
        emulateHelper.stopEmulate(viewModelScope)
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private suspend fun calculateTimeoutAndStartEmulate(
        scope: CoroutineScope,
        config: EmulateConfig,
        oneTimePress: Boolean,
        emulateHelper: EmulateHelper
    ): Boolean {
        val timeout = config.minEmulateTime
        val appStarted: Boolean?
        try {
            appStarted = emulateHelper.startEmulate(
                scope = scope,
                config = config,
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
            emulateHelper.stopEmulateForce(
                isPressRelease = oneTimePress
            )
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return false
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce(
                isPressRelease = oneTimePress
            )
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return false
        } catch (fatal: Throwable) {
            error(fatal) { "Handle fatal exception on emulate infrared" }
            emulateHelper.stopEmulateForce(
                isPressRelease = oneTimePress
            )
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return false
        }
        if (!appStarted) {
            info { "Failed start emulation" }
            emulateHelper.stopEmulateForce(
                isPressRelease = oneTimePress
            )
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
        }

        return appStarted
    }
}
