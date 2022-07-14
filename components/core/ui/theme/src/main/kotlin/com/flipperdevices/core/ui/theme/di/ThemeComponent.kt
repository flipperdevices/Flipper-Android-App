package com.flipperdevices.core.ui.theme.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ThemeComponent {
    fun inject(viewModel: ThemeViewModel)
}
