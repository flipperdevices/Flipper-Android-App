package com.flipperdevices.archive.search.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.archive.search.composable.ComposableSearch
import com.flipperdevices.archive.search.di.SearchComponent
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.archive.search.viewmodel.SearchViewModelFactory
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.ui.fragment.ComposeFragment
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val IS_EXIT_ON_OPEN_KEY = "exist_on_open"

class SearchFragment : ComposeFragment() {
    private val exitOnOpen by lazy {
        arguments?.getBoolean(IS_EXIT_ON_OPEN_KEY, false) ?: false
    }

    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi

    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModelFactory(exitOnOpen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SearchComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableSearch(synchronizationUiApi, viewModel)
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
