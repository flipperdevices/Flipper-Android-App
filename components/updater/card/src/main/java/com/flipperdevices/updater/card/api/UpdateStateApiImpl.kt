package com.flipperdevices.updater.card.api

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.api.UpdateStateApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@ContributesBinding(AppGraph::class, UpdateStateApi::class)
class UpdateStateApiImpl @Inject constructor(
    private val versionParser: FlipperVersionProviderApi,
    private val updaterApi: UpdaterApi
) : UpdateStateApi {
    override fun getFlipperUpdateState(
        serviceApi: FlipperServiceApi,
        scope: CoroutineScope
    ): Flow<FlipperUpdateState> {
        return combine(
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            versionParser.getCurrentFlipperVersion(scope, serviceApi),
            updaterApi.getState()
        ) { connectionState, flipperVersion, updaterState ->
            val isReady = connectionState is ConnectionState.Ready &&
                connectionState.supportedState == FlipperSupportedState.READY

            return@combine if (isReady && flipperVersion != null) when (updaterState.state) {
                is UpdatingState.Rebooting -> {
                    updaterApi.onDeviceConnected(
                        flipperVersion
                    )
                    FlipperUpdateState.Ready
                }
                is UpdatingState.Complete -> {
                    FlipperUpdateState.Complete(updaterState.request?.updateTo)
                }
                is UpdatingState.Failed -> {
                    FlipperUpdateState.Failed(updaterState.request?.updateTo)
                }
                else -> FlipperUpdateState.Ready
            } else if (updaterState.state is UpdatingState.Rebooting) {
                FlipperUpdateState.Updating
            } else if (connectionState is ConnectionState.Disconnected) {
                FlipperUpdateState.NotConnected
            } else FlipperUpdateState.ConnectingInProgress
        }
    }
}
