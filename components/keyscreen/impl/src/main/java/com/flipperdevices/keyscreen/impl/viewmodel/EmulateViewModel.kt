package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_REMOTE_EMULATE
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.combine
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyscreen.impl.model.EmulateButtonState
import com.flipperdevices.keyscreen.impl.tasks.CloseEmulateAppTaskHolder
import com.flipperdevices.keyscreen.impl.viewmodel.helpers.EmulateHelper
import com.flipperdevices.protobuf.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class EmulateViewModel @VMInject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper
) : LifecycleViewModel(), LogTagProvider, FlipperBleServiceConsumer {
    override val TAG = "EmulateViewModel"

    private val emulateButtonStateFlow = MutableStateFlow(EmulateButtonState.DISABLED)

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getEmulateButtonStateFlow(): StateFlow<EmulateButtonState> = emulateButtonStateFlow

    fun onStartEmulate(flipperKey: FlipperKey) {
        val fileType = flipperKey.path.fileType ?: return

        emulateButtonStateFlow.update {
            when (it) {
                EmulateButtonState.DISABLED -> EmulateButtonState.DISABLED
                EmulateButtonState.INACTIVE -> EmulateButtonState.ACTIVE
                EmulateButtonState.ACTIVE -> return
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                val emulateStarted =
                    emulateHelper.startEmulate(this, it.requestApi, fileType, flipperKey)
                if (!emulateStarted) {
                    emulateHelper.stopEmulate(it.requestApi)
                    emulateButtonStateFlow.emit(EmulateButtonState.INACTIVE)
                }
            }
        }
    }

    fun onStopEmulate() {
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                emulateHelper.stopEmulate(it.requestApi)
                emulateButtonStateFlow.emit(EmulateButtonState.INACTIVE)
            }
        }
    }

    fun onSinglePress(flipperKey: FlipperKey) {
        val fileType = flipperKey.path.fileType ?: return

        emulateButtonStateFlow.update {
            when (it) {
                EmulateButtonState.DISABLED -> EmulateButtonState.DISABLED
                EmulateButtonState.INACTIVE -> EmulateButtonState.ACTIVE
                EmulateButtonState.ACTIVE -> return
            }
        }

        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch(Dispatchers.Default) {
                emulateHelper.startEmulate(this, it.requestApi, fileType, flipperKey)
                emulateHelper.stopEmulate(it.requestApi)
                emulateButtonStateFlow.emit(EmulateButtonState.INACTIVE)
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperVersionApi.getVersionInformationFlow()
            .combine(serviceApi.connectionInformationApi.getConnectionStateFlow())
            .onEach { (versionInformation, connectionState) ->
                val buttonEnabled = connectionState is ConnectionState.Ready &&
                    connectionState.supportedState == FlipperSupportedState.READY &&
                    versionInformation != null &&
                    versionInformation >= API_SUPPORTED_REMOTE_EMULATE

                emulateButtonStateFlow.update { emulateButtonState ->
                    if (buttonEnabled) {
                        if (emulateButtonState == EmulateButtonState.DISABLED) {
                            EmulateButtonState.INACTIVE
                        } else emulateButtonState
                    } else {
                        EmulateButtonState.DISABLED
                    }
                }
            }.launchIn(viewModelScope)

        serviceApi.requestApi.notificationFlow().onEach {
            if (it.hasAppStateResponse()) {
                if (it.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    emulateButtonStateFlow.update {
                        if (it == EmulateButtonState.ACTIVE) {
                            EmulateButtonState.INACTIVE
                        } else it
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        if (emulateButtonStateFlow.value == EmulateButtonState.ACTIVE) {
            CloseEmulateAppTaskHolder.closeEmulateApp(serviceProvider, emulateHelper)
        }
        super.onCleared()
    }
}
