package com.flipperdevices.bridge.synchronization.ui.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.bridge.synchronization.ui.composable.ComposableItemSynchronizationState
import com.flipperdevices.bridge.synchronization.ui.model.ItemSynchronizationState
import com.flipperdevices.bridge.synchronization.ui.viewmodel.ItemSynchronizationStateMapper
import com.flipperdevices.bridge.synchronization.ui.viewmodel.SynchronizationStateViewModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class)
class SynchronizationUiApiImpl @Inject constructor(
    private val synchronizationStateViewModelProvider: Provider<SynchronizationStateViewModel>
) : SynchronizationUiApi {
    @Composable
    override fun RenderSynchronizationState(
        componentContext: ComponentContext,
        keyPath: FlipperKeyPath,
        withText: Boolean,
    ) {
        val synchronizationViewModel = componentContext.viewModelWithFactory(null) {
            synchronizationStateViewModelProvider.get()
        }
        val state by synchronizationViewModel.getSynchronizationState(keyPath).collectAsState(
            initial = ItemSynchronizationState.NOT_SYNCHRONIZED
        )
        ComposableItemSynchronizationState(state, withText)
    }

    @Composable
    override fun RenderSynchronizationState(
        synced: Boolean,
        synchronizationState: SynchronizationState,
        withText: Boolean
    ) {
        ComposableItemSynchronizationState(
            itemSynchronizationState = ItemSynchronizationStateMapper.getItemSynchronizedState(
                synchronized = synced,
                synchronizationState
            ),
            withText = withText
        )
    }
}
