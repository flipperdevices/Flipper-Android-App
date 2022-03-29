package com.flipperdevices.bridge.synchronization.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface SynchronizationUiApi {
    @Composable
    fun RenderSynchronizationState(
        keyPath: FlipperKeyPath,
        withText: Boolean
    )

    @Composable
    fun RenderSynchronizationState(
        synced: Boolean,
        synchronizationState: SynchronizationState,
        withText: Boolean
    )
}
