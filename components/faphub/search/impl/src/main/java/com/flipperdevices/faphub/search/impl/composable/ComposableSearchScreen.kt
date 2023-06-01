package com.flipperdevices.faphub.search.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar
import com.flipperdevices.faphub.appcard.composable.paging.ComposableFapsList
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.search.impl.R
import com.flipperdevices.faphub.search.impl.viewmodel.FapHubSearchViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableSearchScreen(
    onBack: () -> Unit,
    onFapItemClick: (FapItemShort) -> Unit,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    val viewModel = tangleViewModel<FapHubSearchViewModel>()
    val fapsList = viewModel.faps.collectAsLazyPagingItems()

    Column(modifier = modifier) {
        ComposableSearchBar(
            hint = stringResource(R.string.faphub_search_hint),
            onChangeText = viewModel::onChangeSearchText,
            onBack = onBack
        )
        LazyColumn {
            ComposableFapsList(fapsList, onFapItemClick, installationButton)
        }
    }
}
