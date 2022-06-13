package com.flipperdevices.info.impl.compose.updater

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.InfoElementCard

@Composable
fun ComposableUpdaterReboot(modifier: Modifier) {
    InfoElementCard(
        modifier = modifier,
        titleId = R.string.info_device_updater_title
    ) {
        val title = stringResource(R.string.info_device_updater_reboot_title) + animatedDots()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.pic_reboot),
                contentDescription = title
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = colorResource(DesignSystem.color.black_100),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.info_device_updater_reboot_desc),
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = colorResource(DesignSystem.color.black_30),
                textAlign = TextAlign.Center
            )
        }
    }
}
