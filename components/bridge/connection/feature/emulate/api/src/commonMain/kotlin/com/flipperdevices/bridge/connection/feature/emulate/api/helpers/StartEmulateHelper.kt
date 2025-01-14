package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.core.data.SemVer
import kotlinx.coroutines.CoroutineScope

interface StartEmulateHelper {
    @Suppress("LongParameterList")
    suspend fun onStart(
        scope: CoroutineScope,
        config: EmulateConfig,
        onStop: suspend () -> Unit,
        onResultTime: (Long) -> Unit
    ): Boolean

    companion object {
        val API_SUPPORTED_INFRARED_EMULATE = SemVer(
            majorVersion = 0,
            minorVersion = 21
        )
    }
}
