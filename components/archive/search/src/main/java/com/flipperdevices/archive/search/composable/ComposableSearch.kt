package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi

@Composable
fun ComposableSearch(
    synchronizationUiApi: SynchronizationUiApi,
    searchViewModel: SearchViewModel
) {
    Column {
        ComposableSearchBar(searchViewModel)
        ComposableSearchContent(
            Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            synchronizationUiApi,
            searchViewModel
        )
    }
}
