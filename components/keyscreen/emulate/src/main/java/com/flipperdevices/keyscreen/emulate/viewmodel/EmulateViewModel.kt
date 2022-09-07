package com.flipperdevices.keyscreen.emulate.viewmodel

import android.app.Application as FlipperApp
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_REMOTE_EMULATE
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.model.EmulateProgress
import com.flipperdevices.keyscreen.emulate.model.LoadingState
import com.flipperdevices.keyscreen.emulate.tasks.CloseEmulateAppTaskHolder
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.AlreadyOpenedAppException
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.EmulateHelper
import com.flipperdevices.protobuf.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val VIBRATOR_TIME = 100L

abstract class EmulateViewModel(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val synchronizationApi: SynchronizationApi,
    application: FlipperApp
) : LifecycleViewModel(), LogTagProvider, FlipperBleServiceConsumer {

    protected val emulateButtonStateFlow =
        MutableStateFlow<EmulateButtonState>(EmulateButtonState.Loading(LoadingState.CONNECTING))

    protected val vibrator = ContextCompat.getSystemService(application, Vibrator::class.java)

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getEmulateButtonStateFlow(): StateFlow<EmulateButtonState> = emulateButtonStateFlow

    open fun onStartEmulate(
        flipperKey: FlipperKey
    ) {
        info { "#onStartEmulate" }
        vibrator?.vibrateCompat(VIBRATOR_TIME)

        val fileType = flipperKey.path.keyType ?: return
        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled -> it
                is EmulateButtonState.Loading -> it
                is EmulateButtonState.Inactive -> EmulateButtonState.Active(
                    EmulateProgress.Infinite
                )
                is EmulateButtonState.Active -> return
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                onStartEmulateInternal(this, it.requestApi, fileType, flipperKey)
            }
        }
    }

    protected open suspend fun onStartEmulateInternal(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType,
        flipperKey: FlipperKey
    ) {
        info { "#onStartEmulateInternal" }
        val emulateStarted = try {
            emulateHelper.startEmulate(
                scope,
                requestApi,
                keyType,
                flipperKey,
                minEmulateTime = 0L
            )
        } catch (ignored: AlreadyOpenedAppException) {
            emulateHelper.stopEmulateForce(requestApi)
            emulateButtonStateFlow.emit(EmulateButtonState.AppAlreadyOpenDialog)
            return
        }
        if (!emulateStarted) {
            emulateHelper.stopEmulateForce(requestApi)
        }
    }

    fun onStopEmulate(force: Boolean = false) {
        info { "#onStopEmulate" }
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                if (force) {
                    emulateHelper.stopEmulateForce(it.requestApi)
                } else emulateHelper.stopEmulate(viewModelScope, it.requestApi)
            }
        }
        vibrator?.vibrateCompat(VIBRATOR_TIME)
    }

    fun closeDialog() {
        emulateButtonStateFlow.update {
            if (it == EmulateButtonState.AppAlreadyOpenDialog) {
                EmulateButtonState.Inactive()
            } else it
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
                        } else it
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        if (emulateButtonStateFlow.value is EmulateButtonState.Active) {
            CloseEmulateAppTaskHolder.closeEmulateApp(serviceProvider, emulateHelper)
        }
        super.onCleared()
    }
}
