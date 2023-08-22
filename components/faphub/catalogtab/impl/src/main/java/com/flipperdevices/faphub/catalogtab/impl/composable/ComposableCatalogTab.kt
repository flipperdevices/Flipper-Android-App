package com.flipperdevices.faphub.catalogtab.impl.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.faphub.appcard.composable.paging.ComposableFapsList
import com.flipperdevices.faphub.appcard.composable.paging.ComposableSortChoice
import com.flipperdevices.faphub.catalogtab.impl.R
import com.flipperdevices.faphub.catalogtab.impl.composable.categories.ComposableCategories
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.CategoriesViewModel
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.FapsListViewModel
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCatalogTabScreen(
    onOpenFapItem: (FapItemShort) -> Unit,
    onCategoryClick: (FapCategory) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    val fapsListViewModel = tangleViewModel<FapsListViewModel>()
    val fapsList = fapsListViewModel.faps.collectAsLazyPagingItems()
    val sortType by fapsListViewModel.getSortTypeFlow().collectAsState()

    val categoriesViewModel = tangleViewModel<CategoriesViewModel>()
    val categoriesLoadState by categoriesViewModel.getCategoriesLoadState().collectAsState()
    SwipeRefresh(
        modifier = modifier,
        onRefresh = {
            fapsListViewModel.refreshManifest()
            fapsList.refresh()
            categoriesViewModel.onRefresh()
        }
    ) {
        LazyColumn(modifier = it) {
            ComposableCategories(
                loadState = categoriesLoadState,
                onCategoryClick = onCategoryClick,
                onRetry = categoriesViewModel::onRefresh,
                errorsRenderer = errorsRenderer
            )
            if (fapsList.loadState.refresh !is LoadState.Error) {
                item {
                    ComposableSortChoice(
                        title = stringResource(R.string.faphub_catalog_title),
                        sortType = sortType,
                        onSelectSortType = fapsListViewModel::onSelectSortType
                    )
                }
            }
            ComposableFapsList(
                faps = fapsList,
                onOpenFapItem = onOpenFapItem,
                errorsRenderer = errorsRenderer,
                installationButton = installationButton,
                defaultFapErrorSize = FapErrorSize.IN_LIST
            )
        }
    }
}
