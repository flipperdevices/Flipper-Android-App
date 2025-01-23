package com.flipperdevices.bridge.connection.transport.ble.impl.api

import com.flipperdevices.bridge.connection.transport.ble.api.FBleApi
import com.flipperdevices.bridge.connection.transport.ble.api.GATTCharacteristicAddress
import com.flipperdevices.bridge.connection.transport.ble.impl.meta.FTransportMetaInfoApiImpl
import com.flipperdevices.bridge.connection.transport.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState
import java.util.concurrent.atomic.AtomicBoolean

open class FBleApiImpl(
    private val scope: CoroutineScope,
    private val client: ClientBleGatt,
    private val statusListener: FTransportConnectionStatusListener,
    private val metaInfoGattMap: ImmutableMap<TransportMetaInfoKey, GATTCharacteristicAddress>,
) : FBleApi,
    FTransportMetaInfoApi by FTransportMetaInfoApiImpl(
        client = client,
        metaInfoGattMap = metaInfoGattMap
    ),
    LogTagProvider {
    override val TAG = "FBleApi"
    private val isForceDisconnected = AtomicBoolean(false)

    init {
        info { "Init ble api listener" }
        client.connectionStateWithStatus
            .filterNotNull()
            .onEach { (state, _) ->
                info { "Receive state $state" }
                statusListener.onStatusUpdate(
                    when (state) {
                        GattConnectionState.STATE_DISCONNECTED -> {
                            client.close()
                            FInternalTransportConnectionStatus.Disconnected
                        }

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
        isForceDisconnected.set(true)
        if (client.connectionState.firstOrNull() != GattConnectionState.STATE_DISCONNECTED) {
            client.disconnect()
        }
        client.connectionState
            .filter { connectionState -> connectionState == GattConnectionState.STATE_DISCONNECTED }
            .first()
    }
}
