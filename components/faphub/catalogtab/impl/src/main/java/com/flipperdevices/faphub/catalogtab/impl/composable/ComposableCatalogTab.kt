package com.flipperdevices.faphub.catalogtab.impl.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.faphub.appcard.composable.paging.ComposableFapsList
import com.flipperdevices.faphub.appcard.composable.paging.ComposableSortChoice
import com.flipperdevices.faphub.catalogtab.impl.R
import com.flipperdevices.faphub.catalogtab.impl.composable.categories.ComposableCategories
import com.flipperdevices.faphub.catalogtab.impl.composable.categories.ComposableFullScreenError
import com.flipperdevices.faphub.catalogtab.impl.model.CategoriesLoadState
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.CategoriesViewModel
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.FapsListViewModel
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.errors.api.throwable.toFapHubError
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

@Composable
fun ComposableCatalogTabScreen(
    onOpenFapItem: (FapItemShort) -> Unit,
    onCategoryClick: (FapCategory) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    fapsListViewModel: FapsListViewModel,
    categoriesViewModel: CategoriesViewModel,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    val fapsList = fapsListViewModel.getFapsFlow().collectAsLazyPagingItems()
    val sortType by fapsListViewModel.getSortTypeFlow().collectAsState()
    val categoriesLoadState by categoriesViewModel.getCategoriesLoadState().collectAsState()

    val categoriesFapHubError by remember {
        categoriesViewModel.getCategoriesLoadState()
            .filterIsInstance<CategoriesLoadState.Error>()
            .map { categoriesErrorLoadState -> categoriesErrorLoadState.throwable.toFapHubError() }
    }.collectAsState(initial = null)

    val refreshAll: () -> Unit = {
        fapsListViewModel.refreshManifest()
        fapsList.refresh()
        categoriesViewModel.onRefresh()
    }

    SwipeRefresh(
        modifier = modifier,
        onRefresh = refreshAll
    ) {
        LazyColumn {
            ComposableCategories(
                loadState = categoriesLoadState,
                onCategoryClick = onCategoryClick,
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
                defaultFapErrorSize = FapErrorSize.IN_LIST,
                shouldDisplayError = categoriesFapHubError == null
            )

            categoriesFapHubError?.let { categoriesFapHubError ->
                ComposableFullScreenError(
                    fapHubError = categoriesFapHubError,
                    errorsRenderer = errorsRenderer,
                    onRetry = refreshAll
                )
            }
        }
    }
}
