package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_REMOTE_EMULATE
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.EmulateButtonState
import com.flipperdevices.keyscreen.impl.tasks.CloseEmulateAppTaskHolder
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.protobuf.app.appButtonPressRequest
import com.flipperdevices.protobuf.app.appButtonReleaseRequest
import com.flipperdevices.protobuf.app.appExitRequest
import com.flipperdevices.protobuf.app.appLoadFileRequest
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class EmulateViewModel : LifecycleViewModel(), LogTagProvider, FlipperBleServiceConsumer {
    override val TAG = "EmulateViewModel"

    private val emulateButtonStateFlow = MutableStateFlow(EmulateButtonState.DISABLED)

    private val mutex = Mutex()
    private var emulateJob: Job? = null

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
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
            launchWithLock(mutex, viewModelScope, "start_emulate") {
                emulateJob?.cancelAndJoin()
                emulateJob = viewModelScope.launch {
                    val emulateStarted = startEmulate(it.requestApi, fileType, flipperKey)
                    if (!emulateStarted) {
                        stopEmulate(it.requestApi)
                        emulateButtonStateFlow.emit(EmulateButtonState.INACTIVE)
                    }
                }
            }
        }
    }

    fun onStopEmulate() {
        serviceProvider.provideServiceApi(this) {
            launchWithLock(mutex, viewModelScope, "stop_emulate") {
                emulateJob?.cancelAndJoin()
                stopEmulate(it.requestApi)
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
            launchWithLock(mutex, viewModelScope, "single_press") {
                emulateJob?.cancelAndJoin()
                emulateJob = viewModelScope.launch {
                    startEmulate(it.requestApi, fileType, flipperKey)
                    stopEmulate(it.requestApi)
                    emulateButtonStateFlow.emit(EmulateButtonState.INACTIVE)
                }
            }
        }
    }

    private suspend fun startEmulate(
        requestApi: FlipperRequestApi,
        fileType: FlipperFileType,
        flipperKey: FlipperKey
    ): Boolean {
        val appStartResponse = requestApi.request(
            flowOf(
                main {
                    appStartRequest = startRequest {
                        name = fileType.flipperAppName
                        args = Constants.RPC_START_REQUEST_ARG
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (!appStartResponse.hasEmpty()) {
            error { "Failed start rpc app with error $appStartResponse" }
            return false
        }
        val appLoadFileResponse = requestApi.request(
            flowOf(
                main {
                    appLoadFileRequest = appLoadFileRequest {
                        path = flipperKey.path.getPathOnFlipper()
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (!appLoadFileResponse.hasEmpty()) {
            error { "Failed start key with error $appLoadFileResponse" }
            return false
        }
        if (fileType != FlipperFileType.SUB_GHZ) {
            info { "startEmulateButton: $appLoadFileResponse" }
            return true
        }
        info { "This is subghz, so start press button" }
        val appButtonPressResponse = requestApi.request(
            flowOf(
                main {
                    appButtonPressRequest = appButtonPressRequest {}
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (!appButtonPressResponse.hasEmpty()) {
            error { "Failed press subghz key with error $appButtonPressResponse" }
            return false
        }
        return true
    }

    private suspend fun stopEmulate(requestApi: FlipperRequestApi) {
        requestApi.request(
            flowOf(
                main {
                    appButtonReleaseRequest = appButtonReleaseRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        requestApi.request(
            flowOf(
                main {
                    appExitRequest = appExitRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperVersionApi.getVersionInformationFlow().onEach {
            val buttonEnabled = it != null && it >= API_SUPPORTED_REMOTE_EMULATE

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
        CloseEmulateAppTaskHolder.closeEmulateApp(serviceProvider)
        super.onCleared()
    }
}
