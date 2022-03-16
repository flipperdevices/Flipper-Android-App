package com.flipperdevices.archive.impl.di

import com.flipperdevices.archive.impl.fragments.ArchiveFragment
import com.flipperdevices.archive.impl.viewmodel.CategoryViewModel
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.archive.impl.viewmodel.KeyItemViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface ArchiveComponent {
    fun inject(fragment: ArchiveFragment)
    fun inject(viewModel: GeneralTabViewModel)
    fun inject(viewModel: CategoryViewModel)
    fun inject(viewModel: KeyItemViewModel)
}
