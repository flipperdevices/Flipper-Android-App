package com.flipperdevices.bridge.connection.feature.update.api

interface DisplayApi {
    suspend fun startVirtualDisplay(byteArray: ByteArray): Result<Unit>
    suspend fun stopVirtualDisplay(): Result<Unit>
}
