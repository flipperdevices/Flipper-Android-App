package com.flipperdevices.faphub.appcard.composable.paging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.flipperdevices.core.ui.ktx.ComposeLottiePic
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.AppCard
import com.flipperdevices.faphub.dao.api.model.FapItem

private const val DEFAULT_FAP_COUNT = 20

@Suppress("FunctionNaming")
fun LazyListScope.ComposableFapsList(
    faps: LazyPagingItems<FapItem>,
    onOpenFapItem: (FapItem) -> Unit,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) {
    if (faps.loadState.refresh is LoadState.Loading) {
        items(DEFAULT_FAP_COUNT) {
            AppCard(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                fapItem = null,
                installationButton = { modifier, fontSize ->
                    installationButton(null, modifier, fontSize)
                }
            )
        }
        return
    }

    ComposableLoadedFapsList(
        faps = faps,
        onOpenFapItem = onOpenFapItem,
        installationButton = installationButton
    )

    when (faps.loadState.append) {
        is LoadState.Loading -> item {
            ComposableLoadingItem()
        }
        is LoadState.Error -> item {
            Text("Error: ${(faps.loadState.append as LoadState.Error).error}")
        }
        else -> {}
    }
}

@Suppress("FunctionNaming")
private fun LazyListScope.ComposableLoadedFapsList(
    faps: LazyPagingItems<FapItem>,
    onOpenFapItem: (FapItem) -> Unit,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) {
    val lastIndex = faps.itemCount - 1
    itemsIndexed(faps) { index, item ->
        item?.let {
            AppCard(
                modifier = Modifier
                    .clickable(
                        onClick = { onOpenFapItem(it) }
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                fapItem = it,
                installationButton = { modifier, fontSize ->
                    installationButton(item, modifier, fontSize)
                }
            )
            if (index != lastIndex) {
                ComposableLoadingItemDivider()
            }
        }
    }
}

@Composable
private fun ComposableLoadingItemDivider() = Divider(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
    thickness = 1.dp,
    color = LocalPallet.current.fapHubDividerColor
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
            rollBackPicResId = DesignSystem.drawable.pic_loader,
            tint = LocalPallet.current.fapHubDividerColor
        )
    }
}
