package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.archive.search.R
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar

@Composable
fun ComposableSearch(
    synchronizationUiApi: SynchronizationUiApi,
    searchViewModel: SearchViewModel
) {
    val router = LocalRouter.current
    Column {
        ComposableSearchBar(
            hint = stringResource(R.string.search_field_hint),
            onChangeText = searchViewModel::onChangeText,
            onBack = router::exit
        )
        ComposableSearchContent(
            Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            synchronizationUiApi,
            searchViewModel
        )
    }
}
