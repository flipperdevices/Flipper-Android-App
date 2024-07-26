package com.flipperdevices.bridge.connection.transport.ble.impl.meta

import com.flipperdevices.bridge.connection.transport.ble.api.GATTCharacteristicAddress
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

class FTransportMetaInfoApiImpl(
    private val client: ClientBleGatt,
    private val metaInfoGattMap: ImmutableMap<TransportMetaInfoKey, GATTCharacteristicAddress>
) : FTransportMetaInfoApi {
    override fun get(key: TransportMetaInfoKey): Result<Flow<ByteArray?>> {
        val address = metaInfoGattMap[key]
            ?: return Result.failure(RuntimeException("Can't found provider for $key"))

        val flow = client.services.flatMapLatest {
            it?.findService(address.serviceAddress)
                ?.findCharacteristic(address.characteristicAddress)
                ?.getNotifications() ?: flowOf(null)
        }.map { it?.value }
        return Result.success(flow)
    }
}