package com.flipperdevices.bridge.connection.transport.ble.impl.meta

import android.annotation.SuppressLint
import com.flipperdevices.bridge.connection.transport.ble.api.GATTCharacteristicAddress
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices
import no.nordicsemi.android.kotlin.ble.core.data.BleGattProperty

class FTransportMetaInfoApiImpl(
    private val client: ClientBleGatt,
    private val metaInfoGattMap: ImmutableMap<TransportMetaInfoKey, GATTCharacteristicAddress>
) : FTransportMetaInfoApi, LogTagProvider {
    override val TAG = "FTransportMetaInfoApi"

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun get(key: TransportMetaInfoKey): Result<Flow<ByteArray?>> = runCatching {
        val address = metaInfoGattMap[key]
            ?: return Result.failure(RuntimeException("Can't found provider for $key"))

        val flow = client.services.flatMapLatest {
            getFlow(it, address)
        }
        return@runCatching flow
    }

    @SuppressLint("MissingPermission")
    private suspend fun getFlow(
        bleGattService: ClientBleGattServices?,
        address: GATTCharacteristicAddress
    ): Flow<ByteArray?> {
        val characteristic = bleGattService?.findService(address.serviceAddress)
            ?.findCharacteristic(address.characteristicAddress)

        if (characteristic == null) {
            warn { "Failed found gatt characteristic for $address" }
            return flowOf(null)
        }

        if (characteristic.properties.contains(BleGattProperty.PROPERTY_NOTIFY).not() &&
            characteristic.properties.contains(BleGattProperty.PROPERTY_INDICATE).not()
        ) {
            warn {
                "Not found PROPERTY_NOTIFY or PROPERTY_INDICATE for property $address, " +
                    "so fallback on one-time read value"
            }
            return flowOf(characteristic.read().value)
        }
        info { "Subscribe on $address characteristic" }
        return characteristic.getNotifications().map { it.value }
    }
}
