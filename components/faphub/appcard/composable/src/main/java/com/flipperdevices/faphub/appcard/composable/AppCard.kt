package com.flipperdevices.faphub.appcard.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.flipperdevices.faphub.appcard.composable.components.AppCardScreenshots
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppIcon
import com.flipperdevices.faphub.dao.api.model.FapItemShort

private val DEFAULT_NAME
    get() = String((Array(size = 10) { 'L' }).toCharArray())
private val DEFAULT_DESCRIPTION
    get() = String((Array(size = 200) { 'L' }).toCharArray())

@Composable
fun AppCard(
    fapItem: FapItemShort?,
    modifier: Modifier = Modifier,
    installationButton: @Composable (Modifier) -> Unit
) {
    Column(modifier) {
        AppCardTop(
            fapItem = fapItem,
            installationButton = installationButton
        )
        AppCardScreenshots(
            screenshots = fapItem?.screenshots,
            modifier = Modifier.padding(vertical = 12.dp),
            screenshotModifier = Modifier
                .padding(end = 6.dp)
                .size(width = 170.dp, height = 84.dp),
        )
        Text(
            modifier = if (fapItem == null) Modifier.placeholderConnecting() else Modifier,
            text = fapItem?.shortDescription ?: DEFAULT_DESCRIPTION,
            maxLines = 2,
            style = LocalTypography.current.subtitleR12,
            overflow = TextOverflow.Ellipsis,
            color = LocalPallet.current.text100
        )
    }
}

@Composable
private fun AppCardTop(
    fapItem: FapItemShort?,
    modifier: Modifier = Modifier,
    installationButton: @Composable (modifier: Modifier) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableAppIcon(
            modifier = Modifier.size(42.dp),
            url = fapItem?.picUrl,
            description = fapItem?.name
        )
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .weight(weight = 1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                modifier = if (fapItem == null) Modifier.placeholderConnecting() else Modifier,
                text = fapItem?.name ?: DEFAULT_NAME,
                style = LocalTypography.current.bodyM14,
                color = LocalPallet.current.text100
            )
            ComposableAppCategory(category = fapItem?.category)
        }
        installationButton(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 4.dp)
        )
    }
}

@Preview
@Composable
private fun ComposableAppCardLoadingPreview() {
    FlipperThemeInternal {
        AppCard(null) { }
    }
}
