package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface EmulateHelper {
    fun getCurrentEmulatingKey(): StateFlow<EmulateConfig?>
    suspend fun startEmulate(
        scope: CoroutineScope,
        config: EmulateConfig
    ): Boolean

    suspend fun stopEmulate(
        scope: CoroutineScope,
        isPressRelease: Boolean = false
    )

    suspend fun stopEmulateForce(
        isPressRelease: Boolean = false
    )
}
