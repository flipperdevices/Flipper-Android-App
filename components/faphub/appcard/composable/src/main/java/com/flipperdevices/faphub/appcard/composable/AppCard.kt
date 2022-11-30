package com.flipperdevices.faphub.appcard.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppIcon
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppScreenshot
import com.flipperdevices.faphub.dao.api.model.FapItem

private const val DEFAULT_SCREENSHOT_SIZE = 6
private val DEFAULT_NAME = String((Array(size = 10) { 'L' }).toCharArray())
private val DEFAULT_DESCRIPTION = String((Array(size = 200) { 'L' }).toCharArray())

@Composable
fun AppCard(
    modifier: Modifier,
    fapItem: FapItem?
) {
    Column(modifier) {
        AppCardTop(
            fapItem = fapItem
        )
        AppCardScreenshots(
            modifier = Modifier.padding(vertical = 12.dp),
            screenshots = fapItem?.screenshots
        )
        Text(
            modifier = if (fapItem == null) Modifier.placeholderConnecting() else Modifier,
            text = fapItem?.description ?: DEFAULT_DESCRIPTION,
            maxLines = 2,
            style = LocalTypography.current.subtitleR12,
            overflow = TextOverflow.Ellipsis,
            color = LocalPallet.current.text100
        )
    }
}

@Composable
private fun AppCardTop(
    modifier: Modifier = Modifier,
    fapItem: FapItem?
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableAppIcon(
            url = fapItem?.picUrl,
            description = fapItem?.name
        )
        Column(
            Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                modifier = if (fapItem == null) Modifier.placeholderConnecting() else Modifier,
                text = fapItem?.name ?: DEFAULT_NAME,
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.text100
            )
            ComposableAppCategory(category = fapItem?.category, isLarge = true)
        }
    }
}

@Composable
private fun AppCardScreenshots(
    modifier: Modifier,
    screenshots: List<String>?
) {
    val screenshotModifier = Modifier
        .padding(end = 6.dp)
        .size(width = 170.dp, height = 84.dp)

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

@Preview
@Composable
private fun ComposableAppCardLoadingPreview() {
    FlipperThemeInternal {
        AppCard(Modifier, null)
    }
}
