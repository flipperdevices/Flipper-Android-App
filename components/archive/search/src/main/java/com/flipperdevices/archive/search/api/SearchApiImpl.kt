package com.flipperdevices.archive.search.api

import com.flipperdevices.archive.api.SearchApi
import com.flipperdevices.archive.search.fragments.SearchFragment
import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SearchApiImpl @Inject constructor() : SearchApi {
    override fun getSearchScreen(): Screen {
        return FragmentScreen { SearchFragment() }
    }
}
