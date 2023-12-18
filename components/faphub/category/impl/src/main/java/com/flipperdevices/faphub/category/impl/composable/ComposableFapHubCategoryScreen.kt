package com.flipperdevices.faphub.category.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.flipperdevices.core.ui.ktx.OrangeAppBarWithIcon
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.faphub.appcard.composable.paging.ComposableFapsList
import com.flipperdevices.faphub.appcard.composable.paging.ComposableSortChoice
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFapHubCategory(
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFapItem: (FapItemShort) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    fapsList: LazyPagingItems<FapItemShort>,
    sortType: SortType,
    categoryName: String,
    onSelectSortType: (SortType) -> Unit,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    Column(modifier = modifier) {
        OrangeAppBarWithIcon(
            title = categoryName,
            onBack = onBack,
            endIconId = DesignSystem.drawable.ic_search,
            onEndClick = onOpenSearch
        )
        SwipeRefresh(onRefresh = fapsList::refresh) {
            LazyColumn {
                if (fapsList.loadState.refresh !is LoadState.Error) {
                    item {
                        ComposableSortChoice(
                            title = null,
                            sortType = sortType,
                            onSelectSortType = onSelectSortType
                        )
                    }
                }

                ComposableFapsList(
                    faps = fapsList,
                    onOpenFapItem = onOpenFapItem,
                    installationButton = installationButton,
                    errorsRenderer = errorsRenderer,
                    defaultFapErrorSize = FapErrorSize.FULLSCREEN
                )
            }
        }
    }
}
