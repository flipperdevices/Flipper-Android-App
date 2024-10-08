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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class InfraredViewModel @Inject constructor(
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
    override val TAG = "InfraredViewModel"

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
        info { "#onSinglePress $config" }
        vibrator?.vibrateCompat(
            VIBRATOR_TIME,
            runBlocking { settings.data.first().disabled_vibration }
        )
        if (config.keyType != FlipperKeyType.INFRARED) {
            return
        }

        serviceProvider.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                processSinglePress(serviceApi, this, config)
            }
        }
    }

    private suspend fun processSinglePress(
        serviceApi: FlipperServiceApi,
        scope: CoroutineScope,
        config: EmulateConfig
    ) {
        val activeState = EmulateButtonState.Active(EmulateProgress.Infinite, config)
        emulateButtonStateFlow.update { buttonState ->
            when (buttonState) {
                is EmulateButtonState.Disabled,
                is EmulateButtonState.Loading -> buttonState

                is EmulateButtonState.Inactive -> activeState
                is EmulateButtonState.Active -> {
                    emulateHelper.stopEmulateForce(serviceApi.requestApi)
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
        startEmulateInternal(scope, serviceApi, config)
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
            return false
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return false
        } catch (fatal: Throwable) {
            error(fatal) { "Handle fatal exception on emulate infrared" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return false
        }
        if (!appStarted) {
            info { "Failed start emulation" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
        }

        return appStarted
    }
}
