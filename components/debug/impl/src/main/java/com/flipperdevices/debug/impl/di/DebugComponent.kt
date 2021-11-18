package com.flipperdevices.debug.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.impl.viewmodel.BruteforceViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface DebugComponent {
    fun inject(viewModel: BruteforceViewModel)
}
