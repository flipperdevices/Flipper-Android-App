package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.model.EmulateProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class SubGhzViewModel @VMInject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    application: Application
) : EmulateViewModel(serviceProvider, emulateHelper, synchronizationApi, application) {
    override val TAG = "SubGhzViewModel"

    override suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig
    ) {
        val appStarted = calculateTimeoutAndStartEmulate(
            scope = scope,
            serviceApi = serviceApi,
            config = config,
            oneTimePress = false
        )

        if (!appStarted) {
            emulateHelper.stopEmulateForce(serviceApi.requestApi)
        }
    }

    fun onSinglePress(config: EmulateConfig) {
        info { "#onSinglePress" }
        vibrator?.vibrateCompat(VIBRATOR_TIME)
        if (config.keyType != FlipperKeyType.SUB_GHZ) {
            return
        }

        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled,
                is EmulateButtonState.Loading -> it
                is EmulateButtonState.Inactive -> EmulateButtonState.Active(
                    EmulateProgress.Infinite
                )
                is EmulateButtonState.Active -> {
                    super.onStopEmulate(force = true)
                    return
                }
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
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
    ): Boolean {
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
                            progress = EmulateProgress.GrowingAndStop(timeout)
                        )
                    )
                } else {
                    emulateButtonStateFlow.emit(
                        EmulateButtonState.Active(
                            progress = EmulateProgress.Growing(timeout)
                        )
                    )
                }
            }
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return false
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return false
        } catch (fatal: Throwable) {
            error(fatal) { "Handle fatal exception on emulate subghz" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return false
        }

        return appStarted
    }
}
