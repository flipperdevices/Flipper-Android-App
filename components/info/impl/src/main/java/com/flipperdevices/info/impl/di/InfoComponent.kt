package com.flipperdevices.info.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.impl.main.InfoFragment
import com.flipperdevices.info.impl.main.viewmodel.InfoViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(fragment: InfoFragment)
    fun inject(viewModel: InfoViewModel)
}
