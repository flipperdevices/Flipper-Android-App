package com.flipperdevices.faphub.catalogtab.impl.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.faphub.catalogtab.impl.composable.categories.ComposableCategories
import com.flipperdevices.faphub.catalogtab.impl.composable.faps.ComposableFapsList
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.CategoriesViewModel
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.FapsListViewModel
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCatalogTabScreen(
    onOpenFapItem: (FapItem) -> Unit,
    onCategoryClick: (FapCategory) -> Unit
) {
    val fapsListViewModel = tangleViewModel<FapsListViewModel>()
    val fapsList = fapsListViewModel.faps.collectAsLazyPagingItems()

    val categoriesViewModel = tangleViewModel<CategoriesViewModel>()
    val categoriesLoadState by categoriesViewModel.getCategoriesLoadState().collectAsState()

    LazyColumn {
        ComposableCategories(categoriesLoadState, onCategoryClick)
        ComposableFapsList(fapsList, onOpenFapItem, fapsListViewModel)
    }
}
