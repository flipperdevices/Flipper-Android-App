package com.flipperdevices.bridge.connection.transport.common.api.serial

interface FSerialRestartApi {
    suspend fun restartRpc()
}
