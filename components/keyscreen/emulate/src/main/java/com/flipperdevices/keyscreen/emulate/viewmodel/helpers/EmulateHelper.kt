package com.flipperdevices.keyscreen.emulate.viewmodel.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyscreen.api.EmulateHelper
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
import kotlin.math.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeoutOrNull

private const val APP_STARTED_TIMEOUT_MS = 3 * 1000L // 3 seconds
private const val APP_RETRY_COUNT = 3
private const val APP_RETRY_SLEEP_TIME_MS = 1 * 1000L // 1 second

/**
 *  It is very important for us not to call startEmulate if the application
 *  is already running - the flipper is very sensitive to the order of execution.
 */
@Singleton
@ContributesBinding(AppGraph::class, EmulateHelper::class)
class EmulateHelperImpl @Inject constructor() : EmulateHelper, LogTagProvider {
    override val TAG = "EmulateHelper"

    private var currentKeyEmulating = MutableStateFlow<FlipperFilePath?>(null)

    @Volatile
    private var stopEmulateTimeAllowedMs: Long = 0
    private var stopJob: Job? = null
    private val mutex = Mutex()

    override fun getCurrentEmulatingKey(): StateFlow<FlipperFilePath?> = currentKeyEmulating

    override suspend fun startEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType,
        keyPath: FlipperFilePath,
        minEmulateTime: Long
    ) = withLockResult(mutex, "start") {
        if (currentKeyEmulating.value != null) {
            info { "Emulate already running, start stop" }
            stopEmulateInternal(requestApi)
        }
        currentKeyEmulating.emit(keyPath)
        try {
            return@withLockResult startEmulateInternal(
                scope,
                requestApi,
                keyType,
                keyPath,
                minEmulateTime
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start $keyPath" }
            currentKeyEmulating.emit(null)
            throw throwable
        }
    }

    override suspend fun stopEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi
    ) = withLock(mutex, "schedule_stop") {
        if (stopJob != null) {
            info { "Return from #stopEmulate because stop already in progress" }
            return@withLock
        }
        if (TimeHelper.getNow() > stopEmulateTimeAllowedMs) {
            info {
                "Already passed delay, stop immediately " +
                        "(current: ${TimeHelper.getNow()}/$stopEmulateTimeAllowedMs)"
            }
            stopEmulateInternal(requestApi)
            return@withLock
        }
        stopJob = scope.launch(Dispatchers.Default) {
            try {
                while (TimeHelper.getNow() < stopEmulateTimeAllowedMs) {
                    val delayMs = max(0, stopEmulateTimeAllowedMs - TimeHelper.getNow())
                    info { "Can't stop right now, wait $delayMs ms" }
                    delay(delayMs)
                }
                launchWithLock(mutex, scope, "stop") {
                    stopEmulateInternal(requestApi)
                }
            } finally {
                stopJob = null
            }
        }
    }

    override suspend fun stopEmulateForce(
        requestApi: FlipperRequestApi
    ) = withLock(mutex, "force_stop") {
        if (stopJob != null) {
            stopJob?.cancelAndJoin()
            stopJob = null
        }
        stopEmulateInternal(requestApi)
    }

    private suspend fun startEmulateInternal(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType,
        keyPath: FlipperFilePath,
        minEmulateTime: Long
    ): Boolean {
        info { "startEmulateInternal" }

        var appOpen = tryOpenApp(scope, requestApi, keyType)
        var retryCount = 0
        while (!appOpen && retryCount < APP_RETRY_COUNT) {
            info { "Failed open app first time, try $retryCount times" }
            retryCount++
            stopEmulateInternal(requestApi)
            delay(APP_RETRY_SLEEP_TIME_MS)
            appOpen = tryOpenApp(scope, requestApi, keyType)
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
                        path = keyPath.getPathOnFlipper()
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (appLoadFileResponse.commandStatus != Flipper.CommandStatus.OK) {
            error { "Failed start key with error $appLoadFileResponse" }
            return false
        }
        if (keyType != FlipperKeyType.SUB_GHZ) {
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
        stopEmulateTimeAllowedMs = TimeHelper.getNow() + minEmulateTime
        return true
    }

    @Throws(AlreadyOpenedAppException::class)
    private suspend fun tryOpenApp(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType
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
                            name = keyType.flipperAppName
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
        currentKeyEmulating.emit(null)
    }
}
