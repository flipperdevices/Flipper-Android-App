package com.flipperdevices.debug.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.impl.fragments.DebugScreenFragment
import com.flipperdevices.debug.impl.viewmodel.StressTestViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface DebugComponent {
    fun inject(viewModel: StressTestViewModel)
    fun inject(fragment: DebugScreenFragment)
}
