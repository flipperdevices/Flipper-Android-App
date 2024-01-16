package com.flipperdevices.bridge.synchronization.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

@Immutable
interface SynchronizationUiApi {
    @Composable
    fun RenderSynchronizationState(
        componentContext: ComponentContext,
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
