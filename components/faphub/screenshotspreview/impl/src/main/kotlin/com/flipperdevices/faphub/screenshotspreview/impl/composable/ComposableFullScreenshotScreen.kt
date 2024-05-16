package com.flipperdevices.faphub.screenshotspreview.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.faphub.screenshotspreview.impl.composable.content.ComposableFullScreenshotAppBar
import com.flipperdevices.faphub.screenshotspreview.impl.composable.content.ComposableScreenshotsList
import com.flipperdevices.faphub.screenshotspreview.impl.composable.content.ComposableScreenshotsPager
import com.flipperdevices.faphub.screenshotspreview.impl.viewmodel.ImageSelectViewModel
import com.flipperdevices.faphub.screenshotspreview.impl.viewmodel.UrlImageShareViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.net.URL

@Composable
internal fun ComposableFullScreenshotScreen(
    onBack: () -> Unit,
    title: String,
    selected: Int,
    screenshots: ImmutableList<String>,
    urlImageShareViewModel: UrlImageShareViewModel,
    imageSelectViewModel: ImageSelectViewModel
) {
    val pagerState = rememberPagerState(selected) {
        screenshots.size
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        ComposableScreenshotsPager(
            screenshots = screenshots,
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            imageSelectViewModel = imageSelectViewModel
        )

        ComposableScreenshotsList(
            screenshots = screenshots,
            currentPage = pagerState.currentPage,
            modifier = Modifier.align(Alignment.BottomCenter),
            onImageSelected = imageSelectViewModel::onImageSelected
        )

        ComposableFullScreenshotAppBar(
            onBack = onBack,
            title = title,
            itemsAmount = screenshots.size,
            selectedItemIndex = pagerState.currentPage,
            modifier = Modifier.align(Alignment.TopCenter),
            onSaveClicked = {
                val url = screenshots[pagerState.currentPage].let(::URL)
                urlImageShareViewModel.shareUrlImage(url)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenshotScreenPreview() {
    FlipperThemeInternal {
        ComposableFullScreenshotScreen(
            onBack = {},
            screenshots = List(size = 20) {
                "https://catalog.flipperzero.one/api/v0/application/version/assets/660e569e6c9840814200ecb8"
            }.toPersistentList(),
            title = "Snake game",
            selected = 3,
            urlImageShareViewModel = UrlImageShareViewModel(
                applicationContext = LocalContext.current.applicationContext
            ),
            imageSelectViewModel = ImageSelectViewModel()
        )
    }
}
