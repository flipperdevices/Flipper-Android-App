package com.flipperdevices.bridge.connection.common.api

import kotlinx.coroutines.flow.Flow

interface FSerialDeviceApi  {
    suspend fun getReceiveBytesFlow(): Flow<ByteArray>
    suspend fun sendBytes(data: ByteArray)
}