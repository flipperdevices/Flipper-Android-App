package com.flipperdevices.keyscreen.emulate.viewmodel.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.protobuf.app.appButtonPressRequest
import com.flipperdevices.protobuf.app.appButtonReleaseRequest
import com.flipperdevices.protobuf.app.appExitRequest
import com.flipperdevices.protobuf.app.appLoadFileRequest
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeoutOrNull

private const val APP_STARTED_TIMEOUT_MS = 3 * 1000L // 10 seconds
private const val APP_RETRY_COUNT = 3
private const val APP_RETRY_SLEEP_TIME_MS = 1 * 1000L // 1 second

/**
 *  It is very important for us not to call startEmulate if the application
 *  is already running - the flipper is very sensitive to the order of execution.
 */
@Singleton
@ContributesBinding(AppGraph::class)
class EmulateHelper @Inject constructor() : LogTagProvider {
    override val TAG = "EmulateHelper"

    @Volatile
    private var isRunning = false
    private val mutex = Mutex()

    suspend fun startEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        fileType: FlipperKeyType,
        flipperKey: FlipperKey
    ) = withLockResult(mutex, "start") {
        if (isRunning) {
            info { "Emulate already running, start stop" }
            stopEmulateInternal(requestApi)
        }
        isRunning = true
        startEmulateInternal(scope, requestApi, fileType, flipperKey)
    }

    suspend fun stopEmulate(
        requestApi: FlipperRequestApi
    ) = withLock(mutex, "stop") {
        stopEmulateInternal(requestApi)
        isRunning = false
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        fileType: FlipperKeyType,
        flipperKey: FlipperKey
    ): Boolean {
        info { "startEmulateInternal" }

        var appOpen = tryOpenApp(scope, requestApi, fileType)
        var retryCount = 0
        while (!appOpen && retryCount < APP_RETRY_COUNT) {
            info { "Failed open app first time, try $retryCount times" }
            retryCount++
            stopEmulateInternal(requestApi)
            delay(APP_RETRY_SLEEP_TIME_MS)
            appOpen = tryOpenApp(scope, requestApi, fileType)
        }
        if (!appOpen) {
            info { "Failed open app with $retryCount retry times" }
            return false
        }
        info { "App open successful" }

        val appLoadFileResponse = requestApi.request(
            flowOf(
                main {
                    appLoadFileRequest = appLoadFileRequest {
                        path = flipperKey.path.getPathOnFlipper()
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (appLoadFileResponse.commandStatus != Flipper.CommandStatus.OK) {
            error { "Failed start key with error $appLoadFileResponse" }
            return false
        }
        if (fileType != FlipperKeyType.SUB_GHZ) {
            info { "Skip execute button press: $appLoadFileResponse" }
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
        if (appButtonPressResponse.commandStatus != Flipper.CommandStatus.OK) {
            error { "Failed press subghz key with error $appButtonPressResponse" }
            return false
        }
        return true
    }

    @Throws(AlreadyOpenedAppException::class)
    private suspend fun tryOpenApp(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        fileType: FlipperKeyType
    ): Boolean {
        val stateAppFlow = MutableStateFlow(Application.AppState.UNRECOGNIZED)
        val pendingStateJob = requestApi
            .notificationFlow()
            .filter { it.hasAppStateResponse() }
            .onEach {
                info { "Receive app state $it" }
                stateAppFlow.emit(it.appStateResponse.state)
            }.launchIn(scope)
        try {
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
            if (appStartResponse.commandStatus == Flipper.CommandStatus.ERROR_APP_SYSTEM_LOCKED) {
                error { "Handle already opened app" }
                throw AlreadyOpenedAppException()
            }
            if (appStartResponse.commandStatus != Flipper.CommandStatus.OK) {
                error { "Failed start rpc app with error $appStartResponse" }
                return false
            }
            info { "Start waiting for stateAppFlow" }
            val appState = withTimeoutOrNull(APP_STARTED_TIMEOUT_MS) {
                stateAppFlow.filter { it == Application.AppState.APP_STARTED }.first()
            }
            if (appState != null) {
                info { "Receive that app state started" }
                return true
            }
            info { "Failed wait for app state started" }
        } finally {
            pendingStateJob.cancelAndJoin()
        }
        return false
    }

    private suspend fun stopEmulateInternal(requestApi: FlipperRequestApi) {
        info { "stopEmulateInternal" }

        val appButtonResponse = requestApi.request(
            flowOf(
                main {
                    appButtonReleaseRequest = appButtonReleaseRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        info { "App button stop response: $appButtonResponse" }

        val appExitResponse = requestApi.request(
            flowOf(
                main {
                    appExitRequest = appExitRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        info { "App exit response: $appExitResponse" }
    }
}
