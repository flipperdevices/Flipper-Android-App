package com.flipperdevices.updater.card.api

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.api.UpdateStateApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@ContributesBinding(AppGraph::class, UpdateStateApi::class)
class UpdateStateApiImpl @Inject constructor(
    private val versionParser: FlipperVersionProviderApi,
    private val updaterApi: UpdaterApi,
    private val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : UpdateStateApi {
    override fun getFlipperUpdateState(
        scope: CoroutineScope
    ): Flow<FlipperUpdateState> {
        return combine(
            fDeviceOrchestrator.getState(),
            fFeatureProvider.get<FVersionFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FVersionFeatureApi> }
                .mapLatest { status -> status?.featureApi?.getSupportedStateFlow() },
            versionParser.getCurrentFlipperVersion(),
            updaterApi.getState()
        ) { deviceConnection, connectionState, flipperVersion, updaterState ->
            val isReady = connectionState?.value == FlipperSupportedState.READY &&
                deviceConnection is FDeviceConnectStatus.Connected

            return@combine if (isReady && flipperVersion != null) {
                when (updaterState.state) {
                    is UpdatingState.Rebooting -> {
                        updaterApi.onDeviceConnected(
                            flipperVersion
                        )
                        FlipperUpdateState.Ready
                    }

                    is UpdatingState.Complete ->
                        FlipperUpdateState.Complete(updaterState.request?.updateTo)

                    is UpdatingState.Failed ->
                        FlipperUpdateState.Failed(updaterState.request?.updateTo)

                    else -> FlipperUpdateState.Ready
                }
            } else if (updaterState.state is UpdatingState.Rebooting) {
                FlipperUpdateState.Updating
            } else if (deviceConnection is FDeviceConnectStatus.Disconnected) {
                FlipperUpdateState.NotConnected
            } else {
                FlipperUpdateState.ConnectingInProgress
            }
        }
    }
}
