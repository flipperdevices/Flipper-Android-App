package com.flipperdevices.faphub.category.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.core.ui.ktx.OrangeAppBarWithIcon
import com.flipperdevices.faphub.appcard.composable.paging.ComposableFapsList
import com.flipperdevices.faphub.appcard.composable.paging.ComposableSortChoice
import com.flipperdevices.faphub.category.impl.viewmodel.FapHubCategoryViewModel
import com.flipperdevices.faphub.dao.api.model.FapItem
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFapHubCategory(
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFapItem: (FapItem) -> Unit,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) {
    val categoryViewModel = tangleViewModel<FapHubCategoryViewModel>()
    val fapsList = categoryViewModel.faps.collectAsLazyPagingItems()
    val sortType by categoryViewModel.getSortTypeFlow().collectAsState()

    Column(modifier = modifier) {
        OrangeAppBarWithIcon(
            title = categoryViewModel.getCategoryName(),
            onBack = onBack,
            endIconId = DesignSystem.drawable.ic_search,
            onEndClick = onOpenSearch
        )

        LazyColumn {
            item {
                ComposableSortChoice(
                    title = null,
                    sortType = sortType,
                    onSelectSortType = categoryViewModel::onSelectSortType
                )
            }
            ComposableFapsList(fapsList, onOpenFapItem, installationButton)
        }
    }
}
