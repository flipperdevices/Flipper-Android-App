package com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.APP_STARTED_TIMEOUT_MS
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.AppEmulateHelper
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcException
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.protobuf.CommandStatus
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.app.AppState
import com.flipperdevices.protobuf.app.AppStateResponse
import com.flipperdevices.protobuf.app.StartRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

const val RPC_START_REQUEST_ARG = "RPC"

class AppEmulateHelperImpl(
    private val fRpcFeatureApi: FRpcFeatureApi,
) : AppEmulateHelper, LogTagProvider {

    override fun appStateFlow(): Flow<AppStateResponse> {
        return fRpcFeatureApi
            .notificationFlow()
            .mapNotNull { it.app_state_response }
            .onEach { info { "Receive app state $it" } }
    }

    override val TAG: String = "AppEmulateHelper"

    @Throws(AlreadyOpenedAppException::class)
    override suspend fun tryOpenApp(
        scope: CoroutineScope,
        keyType: FlipperKeyType
    ): Boolean {
        val stateAppFlow = MutableStateFlow(AppState.fromValue(-1))
        val pendingStateJob = appStateFlow()
            .onEach { stateAppFlow.emit(it.state) }
            .launchIn(scope)
        try {
            val appStartResponse = fRpcFeatureApi.requestOnce(
                Main(
                    app_start_request = StartRequest(
                        name = keyType.flipperAppName,
                        args = RPC_START_REQUEST_ARG
                    )
                ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
            ).getOrThrow()

            return processOpenAppResult(appStartResponse) {
                stateAppFlow.filter { it == AppState.APP_STARTED }.first()
            }
        } catch (e: FRpcException) {
            return processOpenAppResult(e.response) {
                stateAppFlow.filter { it == AppState.APP_STARTED }.first()
            }
        } catch (e: Exception) {
            error(e) { "#tryOpenApp unknown exception" }
            return false
        } finally {
            pendingStateJob.cancelAndJoin()
        }
    }

    @Throws(AlreadyOpenedAppException::class)
    private suspend fun processOpenAppResult(
        appStartResponse: Main,
        onAppTimeout: suspend () -> Unit,
    ): Boolean {
        if (appStartResponse.command_status == CommandStatus.ERROR_APP_SYSTEM_LOCKED) {
            error { "Handle already opened app" }
            throw AlreadyOpenedAppException()
        }

        if (appStartResponse.command_status != CommandStatus.OK) {
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
