package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

const val SUBGHZ_DEFAULT_TIMEOUT_MS = 500L
const val INFRARED_DEFAULT_TIMEOUT_MS = 500L

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
