package com.flipperdevices.archive.search.di

import com.flipperdevices.archive.search.fragments.SearchFragment
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface SearchComponent {
    fun inject(fragment: SearchFragment)
    fun inject(viewModel: SearchViewModel)
}
