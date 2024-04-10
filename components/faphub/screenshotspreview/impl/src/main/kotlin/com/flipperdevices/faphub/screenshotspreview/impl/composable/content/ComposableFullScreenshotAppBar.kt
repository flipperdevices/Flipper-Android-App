package com.flipperdevices.faphub.screenshotspreview.impl.composable.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.screenshotspreview.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableFullScreenshotAppBar(
    onBack: () -> Unit,
    onSaveClicked: () -> Unit,
    title: String,
    itemsAmount: Int,
    selectedItemIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(LocalPallet.current.background)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickableRipple(bounded = false, onClick = onBack),
            painter = painterResource(DesignSystem.drawable.ic_back),
            contentDescription = null.orEmpty()
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = LocalPallet.current.text88,
                style = LocalTypography.current.titleEB18
            )

            Text(
                text = stringResource(
                    R.string.screenshotspreview_list_scroll_progress,
                    selectedItemIndex + 1,
                    itemsAmount
                ),
                color = LocalPallet.current.text88,
                style = LocalTypography.current.subtitleM12
            )
        }

        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickableRipple(bounded = false, onClick = onSaveClicked),
            tint = LocalPallet.current.text100,
            painter = painterResource(R.drawable.ic_share),
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun FullScreenshotAppBar() {
    FlipperThemeInternal {
        ComposableFullScreenshotAppBar(
            onBack = {},
            title = "Snake game",
            itemsAmount = 5,
            selectedItemIndex = 2,
            onSaveClicked = {}
        )
    }
}
