package com.flipperdevices.keyscreen.emulate.viewmodel

import android.app.Application as FlipperApp
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_REMOTE_EMULATE
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.model.LoadingState
import com.flipperdevices.keyscreen.emulate.tasks.CloseEmulateAppTaskHolder
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.EmulateHelper
import com.flipperdevices.protobuf.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

private const val VIBRATOR_TIME = 100L

class EmulateViewModel @VMInject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val synchronizationApi: SynchronizationApi,
    application: FlipperApp
) : LifecycleViewModel(), LogTagProvider, FlipperBleServiceConsumer {
    override val TAG = "EmulateViewModel"

    private val emulateButtonStateFlow =
        MutableStateFlow<EmulateButtonState>(EmulateButtonState.Loading(LoadingState.CONNECTING))

    private val vibrator = ContextCompat.getSystemService(application, Vibrator::class.java)

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getEmulateButtonStateFlow(): StateFlow<EmulateButtonState> = emulateButtonStateFlow

    fun onStartEmulate(flipperKey: FlipperKey) {
        vibrator?.vibrateCompat(VIBRATOR_TIME)

        val fileType = flipperKey.path.keyType ?: return
        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled -> it
                is EmulateButtonState.Loading -> it
                EmulateButtonState.Inactive -> EmulateButtonState.Active()
                is EmulateButtonState.Active -> return
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                val emulateStarted =
                    emulateHelper.startEmulate(this, it.requestApi, fileType, flipperKey)
                if (!emulateStarted) {
                    emulateHelper.stopEmulate(it.requestApi)
                    emulateButtonStateFlow.emit(EmulateButtonState.Inactive)
                }
            }
        }
    }

    fun onStopEmulate() {
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                emulateHelper.stopEmulate(it.requestApi)
                emulateButtonStateFlow.emit(EmulateButtonState.Inactive)
            }
        }
        vibrator?.vibrateCompat(VIBRATOR_TIME)
    }

    fun onSinglePress(flipperKey: FlipperKey) {
        vibrator?.vibrateCompat(VIBRATOR_TIME)
        val fileType = flipperKey.path.keyType ?: return

        emulateButtonStateFlow.update {
            when (it) {
                is EmulateButtonState.Disabled -> it
                EmulateButtonState.Inactive -> EmulateButtonState.Active()
                is EmulateButtonState.Active -> return
                is EmulateButtonState.Loading -> it
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                emulateHelper.startEmulate(this, it.requestApi, fileType, flipperKey)
                emulateHelper.stopEmulate(it.requestApi)
                emulateButtonStateFlow.emit(EmulateButtonState.Inactive)
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
                EmulateButtonState.Inactive
            }
        }.onEach { emulateButtonState ->
            emulateButtonStateFlow.emit(emulateButtonState)
        }.launchIn(viewModelScope)

        serviceApi.requestApi.notificationFlow().onEach { unknownMessage ->
            if (unknownMessage.hasAppStateResponse()) {
                if (unknownMessage.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    emulateButtonStateFlow.update {
                        if (it is EmulateButtonState.Active) {
                            EmulateButtonState.Inactive
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
