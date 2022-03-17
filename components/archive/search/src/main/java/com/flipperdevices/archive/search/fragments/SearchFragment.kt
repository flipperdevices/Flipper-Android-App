package com.flipperdevices.archive.search.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.archive.search.composable.ComposableSearch
import com.flipperdevices.core.ui.ComposeFragment

class SearchFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableSearch()
    }
}
