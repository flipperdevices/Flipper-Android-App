package com.flipperdevices.keyemulate.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val APP_STARTED_TIMEOUT_MS = 3 * 1000L // 3 seconds

interface AppEmulateHelper {
    @Throws(AlreadyOpenedAppException::class)
    suspend fun tryOpenApp(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType
    ): Boolean
}

@ContributesBinding(AppGraph::class, AppEmulateHelper::class)
class AppEmulateHelperImpl @Inject constructor() : AppEmulateHelper, LogTagProvider {

    override val TAG: String = "AppEmulateHelper"

    @Throws(AlreadyOpenedAppException::class)
    override suspend fun tryOpenApp(
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
            return processOpenAppResult(appStartResponse) {
                stateAppFlow.filter { it == Application.AppState.APP_STARTED }.first()
            }
        } finally {
            pendingStateJob.cancelAndJoin()
        }
    }

    @Throws(AlreadyOpenedAppException::class)
    private suspend fun processOpenAppResult(
        appStartResponse: Flipper.Main,
        onAppTimeout: suspend () -> Unit,
    ): Boolean {
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
            onAppTimeout()
        }
        if (appState != null) {
            info { "Receive that app state started" }
            return true
        }
        info { "Failed wait for app state started" }
        return false
    }
}
