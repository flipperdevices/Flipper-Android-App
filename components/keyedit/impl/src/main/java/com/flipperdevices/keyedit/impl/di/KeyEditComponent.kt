package com.flipperdevices.keyedit.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface KeyEditComponent {
    fun inject(viewModel: KeyEditViewModel)
}
