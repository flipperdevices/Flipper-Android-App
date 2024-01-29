package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FlipperSerialApi {
    fun receiveBytesFlow(): Flow<ByteArray>
    fun sendBytes(data: ByteArray)
    suspend fun getSpeed(): StateFlow<FlipperSerialSpeed>
}
