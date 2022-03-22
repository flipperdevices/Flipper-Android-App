package com.flipperdevices.debug.stresstest.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.stresstest.viewmodel.StressTestViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface StressTestComponent {
    fun inject(viewModel: StressTestViewModel)
}
