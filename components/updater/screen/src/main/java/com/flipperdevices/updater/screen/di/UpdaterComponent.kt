package com.flipperdevices.updater.screen.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.screen.fragments.UpdaterFragment
import com.flipperdevices.updater.screen.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface UpdaterComponent {
    fun inject(viewModel: UpdateCardViewModel)
    fun inject(viewModel: UpdaterViewModel)
    fun inject(fragment: UpdaterFragment)
}
