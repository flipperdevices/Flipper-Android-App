package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.card.R

@Composable
fun ComposableUpdaterReboot(modifier: Modifier) {
    InfoElementCard(
        modifier = modifier,
        titleId = R.string.updater_card_updater_title
    ) {
        val title = stringResource(R.string.updater_card_updater_reboot_title) + animatedDots()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(DesignSystem.drawable.pic_reboot),
                contentDescription = title
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                text = title,
                style = LocalTypography.current.bodyM14,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.updater_card_updater_reboot_desc),
                style = LocalTypography.current.bodyR14,
                color = LocalPallet.current.text30,
                textAlign = TextAlign.Center
            )
        }
    }
}
