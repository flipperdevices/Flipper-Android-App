package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SubGhzViewModel @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: Application,
    private val settings: DataStore<Settings>
) : EmulateViewModel(
    serviceProvider,
    emulateHelper,
    synchronizationApi,
    closeEmulateAppTaskHolder,
    application,
    settings
) {
    override val TAG = "SubGhzViewModel"

    override suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig
    ) {
        calculateTimeoutAndStartEmulate(
            scope = scope,
            serviceApi = serviceApi,
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

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                startEmulateInternal(this, it, config)
            }
        }
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig,
    ) {
        calculateTimeoutAndStartEmulate(scope, serviceApi, config, oneTimePress = true)
        emulateHelper.stopEmulate(viewModelScope, serviceApi.requestApi)
    }

    private suspend fun calculateTimeoutAndStartEmulate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig,
        oneTimePress: Boolean
    ) {
        val requestApi = serviceApi.requestApi
        val timeout = config.minEmulateTime
        val appStarted: Boolean?

        try {
            appStarted = emulateHelper.startEmulate(
                scope,
                serviceApi,
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
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return
        } catch (fatal: Throwable) {
            error(fatal) { "Handle fatal exception on emulate subghz" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return
        }

        if (!appStarted) {
            info { "Failed start app without crash" }
            emulateHelper.stopEmulateForce(serviceApi.requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
        }
    }
}
