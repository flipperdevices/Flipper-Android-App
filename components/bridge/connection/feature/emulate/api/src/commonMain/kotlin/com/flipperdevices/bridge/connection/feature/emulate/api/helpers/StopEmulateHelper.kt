package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

interface StopEmulateHelper {
    suspend fun onStop(
        isPressRelease: Boolean = false
    )
}
