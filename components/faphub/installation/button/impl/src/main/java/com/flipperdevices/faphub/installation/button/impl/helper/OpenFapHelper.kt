package com.flipperdevices.faphub.installation.button.impl.helper

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface OpenFapHelper {
    fun getOpenFapState(): StateFlow<OpenFapState>
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
    private val serviceProvider: FlipperServiceProvider,
) : OpenFapHelper {
    private val openFapState = MutableStateFlow<OpenFapState>(OpenFapState.NotSupported)
    override fun getOpenFapState(): StateFlow<OpenFapState> = openFapState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        processSupportRPC()
    }

    private fun processSupportRPC() {
        scope.launch(Dispatchers.Default) {
            val serviceApi = serviceProvider.getServiceApi()
            val version = serviceApi.flipperVersionApi.getVersionInformationFlow().first()

            if (version != null && version >= Constants.API_SUPPORTED_LOAD_FAP) {
                openFapState.emit(OpenFapState.Ready)
            }
        }
    }

    override suspend fun loadFap(
        config: FapButtonConfig,
        onSuccess: suspend () -> Unit,
        onBusy: suspend () -> Unit,
        onError: suspend () -> Unit,
    ) {
        openFapState.emit(OpenFapState.InProgress(config.applicationUid))

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
            Flipper.CommandStatus.OK -> {
                onSuccess()
                openFapState.emit(OpenFapState.Ready)
            }
            Flipper.CommandStatus.ERROR_APP_SYSTEM_LOCKED -> {
                onBusy()
                openFapState.emit(OpenFapState.Ready)
            }
            else -> {
                onError()
                openFapState.emit(OpenFapState.Ready)
            }
        }
    }
}
