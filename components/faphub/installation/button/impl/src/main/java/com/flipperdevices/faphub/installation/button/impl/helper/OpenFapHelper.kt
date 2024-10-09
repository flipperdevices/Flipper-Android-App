package com.flipperdevices.faphub.installation.button.impl.helper

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapResult
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
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
        onResult: (OpenFapResult) -> Unit
    )
}

@Singleton
@ContributesBinding(AppGraph::class, OpenFapHelper::class)
class OpenFapHelperImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider
) : OpenFapHelper, LogTagProvider {
    override val TAG: String = "OpenFapHelperImpl"

    private val currentOpenAppFlow = MutableStateFlow<FapButtonConfig?>(null)
    private val rpcVersionFlow = MutableStateFlow<SemVer?>(null)

    private val scope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)

    override fun getOpenFapState(fapButtonConfig: FapButtonConfig?): Flow<OpenFapState> {
        return combine(
            currentOpenAppFlow,
            rpcVersionFlow,
        ) { currentApp, rpcVersion ->
            return@combine when {
                fapButtonConfig == null -> OpenFapState.NotSupported
                rpcVersion == null || rpcVersion < Constants.API_SUPPORTED_LOAD_FAP ->
                    OpenFapState.NotSupported

                fapButtonConfig.version.buildState != FapBuildState.READY ->
                    OpenFapState.NotSupported

                currentApp != null ->
                    OpenFapState.InProgress(currentApp)

                else -> OpenFapState.Ready
            }
        }
    }

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            val serviceApi = serviceProvider.getServiceApi()
            serviceApi.flipperVersionApi.getVersionInformationFlow().collectLatest {
                rpcVersionFlow.emit(it)
            }
        }
    }

    override suspend fun loadFap(
        config: FapButtonConfig,
        onResult: (OpenFapResult) -> Unit
    ) {
        if (currentOpenAppFlow.compareAndSet(null, config).not()) {
            info { "Cannot open because state not ready" }
            return
        }

        val path = "${Constants.PATH.APPS}${config.categoryAlias}/${config.applicationAlias}.fap"
        val serviceApi = serviceProvider.getServiceApi()

        val appLoadResponse = serviceApi.requestApi.request(
            flowOf(
                main {
                    appStartRequest = startRequest {
                        name = path
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )

        val result: OpenFapResult = when (appLoadResponse.commandStatus) {
            Flipper.CommandStatus.OK -> OpenFapResult.AllGood
            Flipper.CommandStatus.ERROR_APP_SYSTEM_LOCKED -> OpenFapResult.FlipperIsBusy
            else -> OpenFapResult.Error
        }
        currentOpenAppFlow.emit(null)
        onResult(result)
    }
}
