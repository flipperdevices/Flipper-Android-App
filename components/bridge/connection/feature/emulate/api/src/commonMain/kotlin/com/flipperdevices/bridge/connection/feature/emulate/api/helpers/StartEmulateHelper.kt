package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import kotlinx.coroutines.CoroutineScope

interface StartEmulateHelper {
    @Suppress("LongParameterList")
    suspend fun onStart(
        scope: CoroutineScope,
        config: EmulateConfig,
        onStop: suspend () -> Unit,
        onResultTime: (Long) -> Unit
    ): Boolean
}
