package com.flipperdevices.info.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.impl.main.viewmodel.InfoViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(viewModel: InfoViewModel)
}
