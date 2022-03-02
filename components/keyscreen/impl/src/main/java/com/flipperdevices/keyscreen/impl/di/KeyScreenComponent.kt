package com.flipperdevices.keyscreen.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.impl.fragments.KeyScreenFragment
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface KeyScreenComponent {
    fun inject(fragment: KeyScreenFragment)
    fun inject(viewModel: KeyScreenViewModel)
}
