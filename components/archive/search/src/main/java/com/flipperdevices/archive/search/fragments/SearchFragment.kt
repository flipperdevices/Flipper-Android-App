package com.flipperdevices.archive.search.fragments

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.archive.search.composable.ComposableSearch
import com.flipperdevices.archive.search.di.SearchComponent
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.fragment.ComposeFragment
import javax.inject.Inject

class SearchFragment : ComposeFragment() {
    @Inject
    lateinit var synchronizationUiApi: SynchronizationUiApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<SearchComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableSearch(synchronizationUiApi)
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.background
}
