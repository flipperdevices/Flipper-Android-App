package com.flipperdevices.faphub.screenshotspreview.impl.composable.content

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppScreenshot
import com.flipperdevices.faphub.screenshotspreview.impl.viewmodel.ImageSelectViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

private const val SIXTEEN_NINE_RATIO = 16f / 9f

@Composable
internal fun ComposableScreenshotsPager(
    screenshots: ImmutableList<String>,
    pagerState: PagerState,
    imageSelectViewModel: ImageSelectViewModel,
    modifier: Modifier = Modifier,
) {
    var isScaleEnabled by remember { mutableStateOf(false) }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = !isScaleEnabled
    ) { index ->
        val screenshotUrl = screenshots[index]
        val zoomState = rememberZoomState()
        isScaleEnabled = zoomState.scale != 1f
        LaunchedEffect(imageSelectViewModel) {
            imageSelectViewModel.eventFlow
                .onEach { event ->
                    when (event) {
                        is ImageSelectViewModel.Event.ImageSelected -> {
                            zoomState.reset().join()
                            pagerState.scrollToPage(event.index)
                        }
                    }
                }.launchIn(this)
        }
        ComposableAppScreenshot(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .fillMaxWidth()
                .aspectRatio(SIXTEEN_NINE_RATIO)
                .zoomable(
                    zoomState = zoomState,
                    enableOneFingerZoom = false,
                    scrollGesturePropagation = ScrollGesturePropagation.ContentEdge
                ),
            url = screenshotUrl
        )
    }
}
