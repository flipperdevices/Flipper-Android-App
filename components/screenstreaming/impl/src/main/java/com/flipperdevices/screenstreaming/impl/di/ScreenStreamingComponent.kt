package com.flipperdevices.screenstreaming.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ScreenStreamingComponent {
    fun inject(viewModel: ScreenStreamingViewModel)
}
