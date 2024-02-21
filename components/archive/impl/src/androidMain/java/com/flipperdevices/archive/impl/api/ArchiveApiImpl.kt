package com.flipperdevices.archive.impl.api

import androidx.compose.foundation.lazy.LazyListScope
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.archive.impl.composable.page.ComposableKeysGrid
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ArchiveApiImpl @Inject constructor(
    private val synchronizationUiApi: SynchronizationUiApi
) : ArchiveApi {
    override fun LazyListScope.ComposableKeysGridWithSynchronization(
        keys: List<FlipperKey>,
        synchronizationState: SynchronizationState,
        onKeyOpen: (FlipperKeyPath) -> Unit
    ) {
        ComposableKeysGrid(
            keys,
            synchronizationUiApi,
            synchronizationState,
            onKeyOpen
        )
    }
}
