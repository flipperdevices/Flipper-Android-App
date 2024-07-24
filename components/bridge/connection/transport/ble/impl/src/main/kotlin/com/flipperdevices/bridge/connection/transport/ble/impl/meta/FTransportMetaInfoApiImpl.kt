package com.flipperdevices.bridge.connection.transport.ble.impl.meta

import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import kotlinx.coroutines.flow.Flow
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

class FTransportMetaInfoApiImpl(
    private val client: ClientBleGatt
) : FTransportMetaInfoApi {
    override fun get(key: TransportMetaInfoKey): Flow<String> {
       //client.services.map
    }
}