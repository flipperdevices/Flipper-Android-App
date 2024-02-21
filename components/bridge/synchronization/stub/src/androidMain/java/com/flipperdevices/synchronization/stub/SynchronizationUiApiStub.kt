package com.flipperdevices.synchronization.stub

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SynchronizationUiApiStub @Inject constructor() : SynchronizationUiApi {
    @Composable
    override fun RenderSynchronizationState(
        componentContext: ComponentContext,
        keyPath: FlipperKeyPath,
        withText: Boolean
    ) = Unit

    @Composable
    override fun RenderSynchronizationState(
        synced: Boolean,
        synchronizationState: SynchronizationState,
        withText: Boolean
    ) = Unit
}
