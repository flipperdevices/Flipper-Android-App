package com.flipperdevices.faphub.installation.button.impl.helper

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.startRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface LoadFapHelper {
    suspend fun loadFap(
        serviceApi: FlipperServiceApi,
        scope: CoroutineScope,
        config: FapButtonConfig,
        onSuccess: suspend () -> Unit,
        onBusy: suspend () -> Unit,
    )
}

@ContributesBinding(AppGraph::class)
class LoadFapHelperImpl @Inject constructor() : LoadFapHelper {
    override suspend fun loadFap(
        serviceApi: FlipperServiceApi,
        scope: CoroutineScope,
        config: FapButtonConfig,
        onSuccess: suspend () -> Unit,
        onBusy: suspend () -> Unit
    ) {
        val appLoadResponse = serviceApi.requestApi.request(
            flowOf(
                main {
                    appStartRequest = startRequest {
                        name = config.getFlipperPath()
                        args = Constants.RPC_START_REQUEST_ARG
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )

        when (appLoadResponse.commandStatus) {
            Flipper.CommandStatus.OK -> {
                onSuccess()
                return
            }
            Flipper.CommandStatus.ERROR_APP_SYSTEM_LOCKED -> {
                onBusy()
                return
            }
            else -> { return }
        }
    }
}
