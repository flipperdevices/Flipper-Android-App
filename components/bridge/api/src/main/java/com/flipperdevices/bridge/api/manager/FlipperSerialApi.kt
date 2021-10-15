package com.flipperdevices.bridge.api.manager

import kotlinx.coroutines.flow.Flow


interface FlipperSerialApi {
    fun receiveBytesFlow(): Flow<ByteArray>
    fun sendBytes(data: ByteArray)
}