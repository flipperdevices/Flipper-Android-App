package com.flipperdevices.faphub.search.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar
import com.flipperdevices.faphub.appcard.composable.paging.ComposableFapsList
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.search.impl.R
import com.flipperdevices.faphub.search.impl.model.RequestTooSmallException
import com.flipperdevices.faphub.search.impl.viewmodel.FapHubSearchViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableSearchScreen(
    onBack: () -> Unit,
    onFapItemClick: (FapItemShort) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
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
            if (fapsList.loadState.append.endOfPaginationReached && fapsList.itemCount == 0) {
                item {
                    val request by viewModel.getSearchRequest().collectAsState()
                    ComposableResultEmpty(
                        request = request,
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            } else {
                val loadError = (fapsList.loadState.refresh as? LoadState.Error)?.error
                when (loadError) {
                    is RequestTooSmallException -> item {
                        ComposableSearchRequestTooSmall(Modifier.fillParentMaxSize())
                    }

                    else -> ComposableFapsList(
                        faps = fapsList,
                        onOpenFapItem = onFapItemClick,
                        installationButton = installationButton,
                        errorsRenderer = errorsRenderer,
                        defaultFapErrorSize = FapErrorSize.FULLSCREEN
                    )
                }
            }
        }
    }
}
