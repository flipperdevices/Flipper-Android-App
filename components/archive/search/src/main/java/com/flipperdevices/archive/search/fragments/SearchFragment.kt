package com.flipperdevices.archive.search.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.archive.search.api.IS_EXIT_ON_OPEN_KEY
import com.flipperdevices.archive.search.composable.ComposableSearch
import com.flipperdevices.archive.search.di.SearchComponent
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.ktx.LocalRouter
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

class SearchFragment : ComposeFragment() {

    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SearchComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        val searchViewModel: SearchViewModel = tangleViewModel()
        val router = LocalRouter.current
        ComposableSearch(
            searchViewModel = searchViewModel,
            synchronizationUiApi = synchronizationUiApi,
            onBack = router::exit,
            onOpenKeyScreen = {
                searchViewModel.openKeyScreen(router, it)
            }
        )
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.background

    companion object {
        fun getInstance(exitOnOpen: Boolean): SearchFragment {
            return SearchFragment().withArgs {
                putBoolean(IS_EXIT_ON_OPEN_KEY, exitOnOpen)
            }
        }
    }
}
