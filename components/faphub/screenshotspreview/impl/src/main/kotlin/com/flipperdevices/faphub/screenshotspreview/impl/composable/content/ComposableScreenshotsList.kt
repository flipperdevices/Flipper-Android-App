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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppScreenshot
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

private const val MAX_SCALE = 1.35f
private const val MIN_SCALE = 1f

private val CARD_HEIGHT = 34.dp
private val CARD_WIDTH = 65.dp

@Composable
private fun IndicatorItem(
    screenshotUrl: String,
    isSelected: Boolean,
    onClicked: () -> Unit
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
                .clickableRipple(onClick = onClicked),
        )
    }
}

@Composable
internal fun ComposableScreenshotsList(
    screenshots: ImmutableList<String>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyRowState = rememberLazyListState()
    LaunchedEffect(pagerState.currentPage) {
        val firstVisibleItemIndex = lazyRowState.firstVisibleItemIndex
        val lastVisibleItemIndex = lazyRowState.layoutInfo.visibleItemsInfo
            .lastOrNull()?.index ?: return@LaunchedEffect
        if (pagerState.currentPage >= lastVisibleItemIndex) {
            lazyRowState.animateScrollToItem(pagerState.currentPage)
        } else if (pagerState.currentPage <= firstVisibleItemIndex) {
            lazyRowState.animateScrollToItem(pagerState.currentPage)
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
            IndicatorItem(
                screenshotUrl = item,
                isSelected = index == pagerState.currentPage,
                onClicked = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }
            )
        }
    }
}
