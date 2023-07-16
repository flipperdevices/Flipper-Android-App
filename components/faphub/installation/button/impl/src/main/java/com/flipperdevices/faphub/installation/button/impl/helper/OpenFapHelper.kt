package com.flipperdevices.faphub.installation.button.impl.helper

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface OpenFapHelper {
    fun getOpenFapState(fapButtonConfig: FapButtonConfig?): Flow<OpenFapState>
    suspend fun loadFap(
        config: FapButtonConfig,
        onSuccess: suspend () -> Unit,
        onBusy: suspend () -> Unit,
        onError: suspend () -> Unit,
    )
}

@Singleton
@ContributesBinding(AppGraph::class)
class OpenFapHelperImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider
) : OpenFapHelper {
    private val currentOpenAppFlow = MutableStateFlow<FapButtonConfig?>(null)
    private val rpcVersionFlow = MutableStateFlow<SemVer?>(null)

    override fun getOpenFapState(fapButtonConfig: FapButtonConfig?): Flow<OpenFapState> {
        return combine(
            currentOpenAppFlow,
            rpcVersionFlow,
        ) { currentApp, rpcVersion ->
            return@combine when {
                fapButtonConfig == null -> {
                    OpenFapState.NotSupported
                }
                rpcVersion == null || rpcVersion < Constants.API_SUPPORTED_LOAD_FAP -> {
                    OpenFapState.NotSupported
                }
                fapButtonConfig.version.buildState != FapBuildState.READY -> {
                    OpenFapState.NotSupported
                }
                currentApp != null -> {
                    OpenFapState.InProgress(fapButtonConfig)
                }
                else -> OpenFapState.Ready
            }
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch(Dispatchers.Default) {
            val serviceApi = serviceProvider.getServiceApi()
            serviceApi.flipperVersionApi.getVersionInformationFlow().collectLatest {
                rpcVersionFlow.emit(it)
            }
        }
    }

    override suspend fun loadFap(
        config: FapButtonConfig,
        onSuccess: suspend () -> Unit,
        onBusy: suspend () -> Unit,
        onError: suspend () -> Unit,
    ) {
        if (currentOpenAppFlow.value != null) {
            info { "Cannot open because state not in ready" }
            return
        }

        currentOpenAppFlow.emit(config)

        val path = "${Constants.PATH.APPS}${config.categoryAlias}/${config.applicationAlias}.fap"
        val serviceApi = serviceProvider.getServiceApi()

        val appLoadResponse = serviceApi.requestApi.request(
            flowOf(
                main {
                    appStartRequest = startRequest {
                        name = path
                        args = Constants.RPC_START_REQUEST_ARG
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )

        when (appLoadResponse.commandStatus) {
            Flipper.CommandStatus.OK -> onSuccess()
            Flipper.CommandStatus.ERROR_APP_SYSTEM_LOCKED -> onBusy()
            else -> onError()
        }
        currentOpenAppFlow.emit(null)
    }
}
