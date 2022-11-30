package com.flipperdevices.faphub.appcard.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppIcon
import com.flipperdevices.faphub.appcard.composable.internal.ComposableAppScreenshot
import com.flipperdevices.faphub.dao.api.model.FapItem

@Composable
fun AppCard(
    modifier: Modifier,
    fapItem: FapItem
) {
    Column(modifier) {
        AppCardTop(fapItem = fapItem)
        AppCardScreenshots(
            modifier = Modifier.padding(vertical = 12.dp),
            screenshots = fapItem.screenshots
        )
        Text(
            text = fapItem.description,
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
    fapItem: FapItem
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableAppIcon(
            url = fapItem.picUrl,
            description = fapItem.name
        )
        Column(
            Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = fapItem.name,
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.text100
            )
            ComposableAppCategory(category = fapItem.category)
        }
    }
}

@Composable
private fun AppCardScreenshots(modifier: Modifier, screenshots: List<String>) = LazyRow(
    modifier = modifier.fillMaxWidth(),
    contentPadding = PaddingValues(end = 8.dp)
) {
    items(screenshots.size) { index ->
        val screenshotUrl = screenshots[index]
        ComposableAppScreenshot(
            modifier = Modifier
                .size(width = 170.dp, height = 84.dp)
                .padding(),
            url = screenshotUrl
        )
    }
}
