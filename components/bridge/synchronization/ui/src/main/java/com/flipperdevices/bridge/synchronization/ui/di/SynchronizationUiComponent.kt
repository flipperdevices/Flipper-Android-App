package com.flipperdevices.bridge.synchronization.ui.di

import com.flipperdevices.bridge.synchronization.ui.viewmodel.SynchronizationStateViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface SynchronizationUiComponent {
    fun inject(viewModel: SynchronizationStateViewModel)
}
