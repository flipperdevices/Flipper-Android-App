package com.flipperdevices.archive.api

import androidx.compose.foundation.lazy.LazyListScope
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState

interface ArchiveApi {
    @Suppress("FunctionName")
    fun LazyListScope.ComposableKeysGridWithSynchronization(
        keys: List<FlipperKey>,
        synchronizationState: SynchronizationState,
        onKeyOpen: (FlipperKeyPath) -> Unit
    )
}
