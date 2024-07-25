package com.flipperdevices.faphub.screenshotspreview.impl.composable.content

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppScreenshot
import kotlinx.collections.immutable.ImmutableList

private const val MAX_SCALE = 1.35f
private const val MIN_SCALE = 1f

private val CARD_HEIGHT = 34.dp
private val CARD_WIDTH = 65.dp

@Composable
private fun IndicatorItem(
    screenshotUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) MAX_SCALE else MIN_SCALE,
        label = "scale"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ComposableAppScreenshot(
            url = screenshotUrl,
            modifier = Modifier
                .width(CARD_WIDTH * scale)
                .height(CARD_HEIGHT * scale)
                .clickableRipple(onClick = onClick),
        )
    }
}

@Composable
internal fun ComposableScreenshotsList(
    screenshots: ImmutableList<String>,
    currentPage: Int,
    modifier: Modifier = Modifier,
    onImageSelect: (Int) -> Unit
) {
    val lazyRowState = rememberLazyListState()
    LaunchedEffect(currentPage) {
        val firstVisibleItemIndex = lazyRowState.firstVisibleItemIndex
        val lastVisibleItemIndex = lazyRowState.layoutInfo.visibleItemsInfo
            .lastOrNull()?.index ?: return@LaunchedEffect
        if (currentPage >= lastVisibleItemIndex) {
            lazyRowState.animateScrollToItem(currentPage)
        } else if (currentPage <= firstVisibleItemIndex) {
            lazyRowState.animateScrollToItem(currentPage)
        }
    }
    LazyRow(
        state = lazyRowState,
        modifier = modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT * MAX_SCALE),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(screenshots) { index, item ->
            val isSelected = index == currentPage
            IndicatorItem(
                screenshotUrl = item,
                isSelected = isSelected,
                onClick = onClick@{
                    if (isSelected) return@onClick
                    onImageSelect(index)
                }
            )
        }
    }
}
