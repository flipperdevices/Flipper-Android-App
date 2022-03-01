package com.flipperdevices.keyscreen.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.impl.viewmodel.edit.KeyEditViewModel
import com.flipperdevices.keyscreen.impl.viewmodel.view.KeyScreenViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface KeyScreenComponent {
    fun inject(viewModel: KeyScreenViewModel)
    fun inject(viewModel: KeyEditViewModel)
}
