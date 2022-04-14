package com.flipperdevices.updater.ui.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface UpdaterComponent {
    fun inject(viewModel: UpdaterViewModel)
}
