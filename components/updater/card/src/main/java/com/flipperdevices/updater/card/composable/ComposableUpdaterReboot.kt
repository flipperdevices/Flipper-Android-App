package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.animatedDots
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.card.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableUpdaterReboot(modifier: Modifier = Modifier) {
    InfoElementCard(
        modifier = modifier,
        titleId = R.string.updater_card_updater_title
    ) {
        val title = stringResource(R.string.updater_card_updater_reboot_title) + animatedDots()

        val imageId = if (MaterialTheme.colors.isLight) {
            DesignSystem.drawable.pic_reboot
        } else {
            DesignSystem.drawable.pic_reboot_dark
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(imageId),
                contentDescription = title
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp),
                text = title,
                style = LocalTypography.current.bodyM14,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(bottom = 16.dp, start = 12.dp, end = 12.dp),
                text = stringResource(R.string.updater_card_updater_reboot_desc),
                style = LocalTypography.current.bodyR14,
                color = LocalPallet.current.text30,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableUpdaterRebootPreview() {
    FlipperThemeInternal {
        ComposableUpdaterReboot(Modifier)
    }
}
