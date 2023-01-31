package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.archive.search.R
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar

@Composable
fun ComposableSearch(
    synchronizationUiApi: SynchronizationUiApi,
    searchViewModel: SearchViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    Column(modifier) {
        ComposableSearchBar(
            hint = stringResource(R.string.search_field_hint),
            onChangeText = searchViewModel::onChangeText,
            onBack = onBack
        )
        ComposableSearchContent(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            synchronizationUiApi = synchronizationUiApi,
            searchViewModel = searchViewModel,
            onOpenKeyScreen = onOpenKeyScreen,
        )
    }
}
