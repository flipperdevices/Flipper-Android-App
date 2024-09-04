package com.flipperdevices.infrared.impl.api

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_INFRARED_EMULATE
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.info
import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@ContributesBinding(AppGraph::class, InfraredConnectionApi::class)
class InfraredConnectionApiImpl @Inject constructor(
    private val synchronizationApi: SynchronizationApi
) : InfraredConnectionApi {
    override fun getState(
        serviceApi: FlipperServiceApi
    ): Flow<InfraredConnectionApi.InfraredEmulateState> {
        return combine(
            serviceApi.flipperVersionApi.getVersionInformationFlow(),
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            synchronizationApi.getSynchronizationState()
        ) { versionInformation, connectionState, synchronizationState ->
            val infraredEmulateState = when {
                connectionState is ConnectionState.Disconnected -> {
                    InfraredConnectionApi.InfraredEmulateState.NOT_CONNECTED
                }

                connectionState !is ConnectionState.Ready ||
                    connectionState.supportedState != FlipperSupportedState.READY -> {
                    InfraredConnectionApi.InfraredEmulateState.CONNECTING
                }

                synchronizationState is SynchronizationState.InProgress -> {
                    InfraredConnectionApi.InfraredEmulateState.SYNCING
                }

                versionInformation == null || versionInformation < API_SUPPORTED_INFRARED_EMULATE -> {
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
