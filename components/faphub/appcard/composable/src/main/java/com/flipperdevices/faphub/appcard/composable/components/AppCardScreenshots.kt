package com.flipperdevices.faphub.appcard.composable.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val DEFAULT_SCREENSHOT_SIZE = 6

@Composable
fun AppCardScreenshots(
    modifier: Modifier,
    screenshotModifier: Modifier,
    screenshots: List<String>?
) {
    LazyRow(
        modifier = modifier.fillMaxWidth()
    ) {
        if (screenshots == null) {
            items(DEFAULT_SCREENSHOT_SIZE) {
                ComposableAppScreenshot(
                    modifier = screenshotModifier,
                    url = null
                )
            }
            return@LazyRow
        }
        items(screenshots.size) { index ->
            val screenshotUrl = screenshots[index]
            ComposableAppScreenshot(
                modifier = screenshotModifier,
                url = screenshotUrl
            )
        }
    }
}
