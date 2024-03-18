package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.ble.api.FBleApi
import com.flipperdevices.bridge.connection.common.api.FSerialDeviceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

class FBleApiImpl(
    private val scope: CoroutineScope,
    private val client: ClientBleGatt
) : FBleApi, FSerialDeviceApi {
    override suspend fun getReceiveBytesFlow(): Flow<ByteArray> {
        TODO("Not yet implemented")
    }

    override suspend fun sendBytes(data: ByteArray) {
        TODO("Not yet implemented")
    }
}