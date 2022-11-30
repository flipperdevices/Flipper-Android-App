package com.flipperdevices.faphub.catalogtab.impl.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.flipperdevices.faphub.catalogtab.impl.composable.faps.ComposableFapsList
import com.flipperdevices.faphub.catalogtab.impl.viewmodel.FapsListViewModel
import com.flipperdevices.faphub.dao.api.model.FapItem
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableCatalogTabScreen(
    onOpenFapItem: (FapItem) -> Unit
) {
    val viewModel = tangleViewModel<FapsListViewModel>()
    val fapsList = viewModel.faps.collectAsLazyPagingItems()

    LazyColumn {
        ComposableFapsList(fapsList, onOpenFapItem, viewModel)
    }
}
