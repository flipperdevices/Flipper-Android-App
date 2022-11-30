package com.flipperdevices.faphub.catalogtab.impl.composable.faps

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.flipperdevices.core.ui.ktx.ComposeLottiePic
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.AppCard
import com.flipperdevices.faphub.appcard.composable.AppCardLoading
import com.flipperdevices.faphub.dao.api.model.FapItem

fun LazyListScope.ComposableFapsList(faps: LazyPagingItems<FapItem>) {
    val lastIndex = faps.itemCount - 1
    itemsIndexed(faps) { index, item ->
        item?.let {
            AppCard(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                fapItem = it
            )
            if (index != lastIndex) {
                ComposableLoadingItemDivider()
            }
        }
    }
    faps.loadState.let { loadState ->
        when {
            loadState.refresh is LoadState.Loading -> item {
                //You can add modifier to manage load state when first time response page is loading
                AppCardLoading(Modifier.fillMaxSize())
            }
            loadState.append is LoadState.Loading -> item {
                //You can add modifier to manage load state when next response page is loading
                ComposableLoadingItem()
            }
            loadState.append is LoadState.Error -> item {
                //You can use modifier to show error message
                Text("Error: ${(loadState.append as LoadState.Error).error}")
            }
        }
    }
}

@Composable
private fun ComposableLoadingItemDivider() = Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
        .background(LocalPallet.current.fapHubDividerColor)
)

@Composable
private fun ComposableLoadingItem() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ComposeLottiePic(
            modifier = Modifier.padding(24.dp),
            picResId = DesignSystem.raw.dots_loader,
            rollBackPicResId = DesignSystem.drawable.pic_loader
        )
    }
}