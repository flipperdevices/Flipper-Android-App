package com.flipperdevices.keyemulate.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.keyemulate.model.EmulateConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

const val SUBGHZ_DEFAULT_TIMEOUT_MS = 500L
const val INFRARED_DEFAULT_TIMEOUT_MS = 500L

interface EmulateHelper {
    fun getCurrentEmulatingKey(): StateFlow<EmulateConfig?>

    /**
     * @param isPressRelease one-time emulate for infrared-only
     */
    suspend fun startEmulate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig,
        isPressRelease: Boolean = false
    ): Boolean

    suspend fun stopEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi
    )

    suspend fun stopEmulateForce(
        requestApi: FlipperRequestApi
    )
}
