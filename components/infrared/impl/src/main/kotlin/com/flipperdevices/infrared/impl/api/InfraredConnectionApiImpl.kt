package com.flipperdevices.infrared.impl.api

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.info
import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class, InfraredConnectionApi::class)
class InfraredConnectionApiImpl @Inject constructor(
    private val synchronizationApi: SynchronizationApi,
    private val fDeviceOrchestrator: FDeviceOrchestrator,
    private val fFeatureProvider: FFeatureProvider
) : InfraredConnectionApi {
    override fun getState(): Flow<InfraredConnectionApi.InfraredEmulateState> {
        return combine(
            fFeatureProvider.get<FVersionFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FVersionFeatureApi> }
                .map { status -> status?.featureApi }
                .flatMapLatest { feature -> feature?.getVersionInformationFlow() ?: flowOf(null) },
            fFeatureProvider.get<FVersionFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FVersionFeatureApi> }
                .map { status -> status?.featureApi }
                .flatMapLatest { feature -> feature?.getSupportedStateFlow() ?: flowOf(null) },
            fDeviceOrchestrator.getState(),
            synchronizationApi.getSynchronizationState(),
            fFeatureProvider.get<FEmulateFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FEmulateFeatureApi> }
                .map { status -> status?.featureApi }
                .flatMapLatest { feature ->
                    feature?.isInfraredEmulationSupported ?: flowOf(false)
                },
        ) { versionInformation,
            supportedState,
            connectionState,
            synchronizationState,
            isInfraredEmulationSupported ->
            val infraredEmulateState = when {
                connectionState is FDeviceConnectStatus.Disconnected -> {
                    InfraredConnectionApi.InfraredEmulateState.NOT_CONNECTED
                }

                connectionState !is FDeviceConnectStatus.Connected ||
                    supportedState != FlipperSupportedState.READY -> {
                    InfraredConnectionApi.InfraredEmulateState.CONNECTING
                }

                synchronizationState is SynchronizationState.InProgress -> {
                    InfraredConnectionApi.InfraredEmulateState.SYNCING
                }

                versionInformation == null || !isInfraredEmulationSupported -> {
                    InfraredConnectionApi.InfraredEmulateState.UPDATE_FLIPPER
                }

                else -> {
                    InfraredConnectionApi.InfraredEmulateState.ALL_GOOD
                }
            }
            info { "#onServiceApiReady $infraredEmulateState" }
            infraredEmulateState
        }
    }
}
