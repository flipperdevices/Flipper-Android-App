package com.flipperdevices.bridge.connection.transport.ble.impl.api

import com.flipperdevices.bridge.connection.transport.ble.api.FBleApi
import com.flipperdevices.bridge.connection.transport.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState

open class FBleApiImpl(
    scope: CoroutineScope,
    private val client: ClientBleGatt,
    private val statusListener: FTransportConnectionStatusListener,
) : FBleApi, LogTagProvider {
    override val TAG = "FBleApi"

    init {
        info { "Init ble api listener" }
        client.connectionStateWithStatus
            .filterNotNull()
            .onEach { (state, status) ->
                info { "Receive state $state" }
                statusListener.onStatusUpdate(
                    when (state) {
                        GattConnectionState.STATE_DISCONNECTED ->
                            FInternalTransportConnectionStatus.Disconnected

                        GattConnectionState.STATE_CONNECTING ->
                            FInternalTransportConnectionStatus.Connecting

                        GattConnectionState.STATE_CONNECTED ->
                            FInternalTransportConnectionStatus.Connected(
                                scope = scope,
                                deviceApi = this
                            )

                        GattConnectionState.STATE_DISCONNECTING ->
                            FInternalTransportConnectionStatus.Disconnecting
                    }
                )
            }.launchIn(scope)
    }

    override suspend fun disconnect() {
        client.disconnect()
        client.close()
    }
}
