package com.flipperdevices.bridge.connection.transport.common.api.serial

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FSerialDeviceApi {
    suspend fun getSpeed(): StateFlow<FlipperSerialSpeed>
    suspend fun getReceiveBytesFlow(): Flow<ByteArray>
    suspend fun sendBytes(data: ByteArray)
    fun getActionNotifier(): FlipperActionNotifier
}
