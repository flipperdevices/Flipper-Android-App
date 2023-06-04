package com.flipperdevices.faphub.catalogtab.impl.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCatalogTabScreen(
    onOpenFapItem: (FapItemShort) -> Unit,
    onCategoryClick: (FapCategory) -> Unit,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    val fapsListViewModel = tangleViewModel<FapsListViewModel>()
    val fapsList = fapsListViewModel.faps.collectAsLazyPagingItems()
    val sortType by fapsListViewModel.getSortTypeFlow().collectAsState()

    val categoriesViewModel = tangleViewModel<CategoriesViewModel>()
    val categoriesLoadState by categoriesViewModel.getCategoriesLoadState().collectAsState()
    SwipeRefresh(onRefresh = {
        fapsListViewModel.refreshManifest()
        fapsList.refresh()
        categoriesViewModel.onRefresh()
    }) {
        LazyColumn(
            modifier = modifier
        ) {
            ComposableCategories(
                categoriesLoadState,
                onCategoryClick,
                onRetry = categoriesViewModel::onRefresh
            )
            item {
                ComposableSortChoice(
                    title = stringResource(R.string.faphub_catalog_title),
                    sortType = sortType,
                    onSelectSortType = fapsListViewModel::onSelectSortType
                )
            }
            ComposableFapsList(fapsList, onOpenFapItem, installationButton)
        }
    }
}
