package com.flipperdevices.updater.screen.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.screen.fragments.UpdaterFragment
import com.flipperdevices.updater.screen.viewmodel.FlipperColorViewModel
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface UpdaterComponent {
    fun inject(viewModel: UpdaterViewModel)
    fun inject(viewModel: FlipperColorViewModel)
    fun inject(fragment: UpdaterFragment)
}
