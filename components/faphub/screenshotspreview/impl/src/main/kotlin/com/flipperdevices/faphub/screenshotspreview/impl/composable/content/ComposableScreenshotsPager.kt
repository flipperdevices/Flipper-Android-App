package com.flipperdevices.faphub.screenshotspreview.impl.composable.content

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppScreenshot
import kotlinx.collections.immutable.ImmutableList
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

private const val SIXTEEN_NINE_RATIO = 16f / 9f

@Composable
internal fun ComposableScreenshotsPager(
    screenshots: ImmutableList<String>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) { index ->
        val screenshotUrl = screenshots[index]
        val zoomState = rememberZoomState()
        ComposableAppScreenshot(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .fillMaxWidth()
                .aspectRatio(SIXTEEN_NINE_RATIO)
                .zoomable(
                    zoomState = zoomState,
                    enableOneFingerZoom = false,
                    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                ),
            url = screenshotUrl
        )
    }
}
