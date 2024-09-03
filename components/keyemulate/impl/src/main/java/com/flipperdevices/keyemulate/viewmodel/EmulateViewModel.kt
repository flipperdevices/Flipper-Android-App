package com.flipperdevices.keyemulate.viewmodel

import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_REMOTE_EMULATE
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.DisableButtonReason
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.keyemulate.model.LoadingState
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import com.flipperdevices.protobuf.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.app.Application as FlipperApp

const val VIBRATOR_TIME = 100L

abstract class EmulateViewModel(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val synchronizationApi: SynchronizationApi,
    private val closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: FlipperApp
) : DecomposeViewModel(), LogTagProvider, FlipperBleServiceConsumer {

    protected val emulateButtonStateFlow =
        MutableStateFlow<EmulateButtonState>(EmulateButtonState.Loading(LoadingState.CONNECTING))

    protected val vibrator = ContextCompat.getSystemService(application, Vibrator::class.java)

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getEmulateButtonStateFlow(): StateFlow<EmulateButtonState> = emulateButtonStateFlow

    open fun onStartEmulate(
        config: EmulateConfig
    ) {
        info { "#onStartEmulate" }
        vibrator?.vibrateCompat(VIBRATOR_TIME)

        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled -> it
                is EmulateButtonState.Loading -> it
                is EmulateButtonState.Inactive -> EmulateButtonState.Active(
                    progress = EmulateProgress.Infinite,
                    config = config
                )
                is EmulateButtonState.Active -> return
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                onStartEmulateInternal(this, it, config)
            }
        }
    }

    protected open suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig
    ) {
        val requestApi = serviceApi.requestApi
        info { "#onStartEmulateInternal" }
        val emulateStarted = try {
            emulateHelper.startEmulate(
                scope,
                serviceApi,
                config
            )
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return
        } catch (ignored: ForbiddenFrequencyException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.ForbiddenFrequencyDialog)
            return
        } catch (fatal: Throwable) {
            error(fatal) { "Unhandled exception on emulate" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
            return
        }
        if (!emulateStarted) {
            info { "Failed start emulation" }
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.Inactive())
        }
    }

    fun onStopEmulate(force: Boolean = false) {
        info { "#onStopEmulate" }
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                if (force) {
                    emulateHelper.stopEmulateForce(it.requestApi)
                } else {
                    emulateHelper.stopEmulate(viewModelScope, it.requestApi)
                }
            }
        }
        vibrator?.vibrateCompat(VIBRATOR_TIME)
    }

    fun closeDialog() {
        emulateButtonStateFlow.update {
            if (it == EmulateButtonState.AppAlreadyOpenDialog ||
                it == EmulateButtonState.ForbiddenFrequencyDialog
            ) {
                EmulateButtonState.Inactive()
            } else {
                it
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        combine(
            serviceApi.flipperVersionApi.getVersionInformationFlow(),
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            synchronizationApi.getSynchronizationState()
        ) { versionInformation, connectionState, synchronizationState ->
            return@combine if (connectionState is ConnectionState.Disconnected) {
                EmulateButtonState.Disabled(DisableButtonReason.NOT_CONNECTED)
            } else if (connectionState !is ConnectionState.Ready ||
                connectionState.supportedState != FlipperSupportedState.READY
            ) {
                EmulateButtonState.Loading(LoadingState.CONNECTING)
            } else if (synchronizationState is SynchronizationState.InProgress) {
                EmulateButtonState.Loading(LoadingState.SYNCING)
            } else if (versionInformation == null ||
                versionInformation < API_SUPPORTED_REMOTE_EMULATE
            ) {
                EmulateButtonState.Disabled(DisableButtonReason.UPDATE_FLIPPER)
            } else {
                EmulateButtonState.Inactive()
            }
        }.onEach { emulateButtonState ->
            emulateButtonStateFlow.emit(emulateButtonState)
        }.launchIn(viewModelScope)

        serviceApi.requestApi.notificationFlow().onEach { unknownMessage ->
            if (unknownMessage.hasAppStateResponse()) {
                if (unknownMessage.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    emulateButtonStateFlow.update {
                        if (it is EmulateButtonState.Active) {
                            EmulateButtonState.Inactive()
                        } else {
                            it
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun onDestroy() {
        if (emulateButtonStateFlow.value is EmulateButtonState.Active) {
            closeEmulateAppTaskHolder.closeEmulateApp(serviceProvider, emulateHelper)
        }
        super.onDestroy()
    }
}
