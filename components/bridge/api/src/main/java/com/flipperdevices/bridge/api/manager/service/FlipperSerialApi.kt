package com.flipperdevices.bridge.api.manager.service

import kotlinx.coroutines.flow.Flow

interface FlipperSerialApi {
    fun receiveBytesFlow(): Flow<ByteArray>
    fun sendBytes(data: ByteArray)
}
