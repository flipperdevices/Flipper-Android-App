package com.flipperdevices.archive.category.di

import com.flipperdevices.archive.category.viewmodels.CategoryViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface CategoryComponent {
    fun inject(viewModel: CategoryViewModel)
}
