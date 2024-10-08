package com.flipperdevices.filemanager.ui.components.sdcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
private fun StorageSizeVerticalText(text: String, size: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = text,
            style = LocalTypography.current.subtitleM10,
            color = LocalPalletV2.current.text.label.secondary
        )
        Text(
            text = size.toFormattedSize(),
            style = LocalTypography.current.bodyM14,
            color = LocalPalletV2.current.text.label.primary
        )
    }
}

@Composable
fun SdCardOkComposable(
    used: Long,
    total: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LocalPalletV2.current.surface.contentCard.body.default)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(weight = 0.6f)) {
            Text(
                text = stringResource(FR.string.sd_card_ok_title),
                style = LocalTypography.current.titleB18,
                color = LocalPalletV2.current.text.title.primary
            )
            Spacer(Modifier.height(18.dp))
            LinearProgressIndicator(
                progress = used / total.coerceAtLeast(1).toFloat(),
                color = LocalPalletV2.current.action.brand.background.primary.default,
                backgroundColor = LocalPalletV2.current.action.brand.background.tertiary.default,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                StorageSizeVerticalText(
                    text = stringResource(FR.string.sd_card_used),
                    size = used
                )
                StorageSizeVerticalText(
                    text = stringResource(FR.string.sd_card_total),
                    size = total
                )
            }
        }
        Box(
            modifier = Modifier.weight(weight = 0.4f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(
                    when {
                        MaterialTheme.colors.isLight -> FR.drawable.ic_sd_card_ok_black
                        else -> FR.drawable.ic_sd_card_ok_white
                    }
                ),
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun SdCardOkComposablePreview() {
    FlipperThemeInternal {
        SdCardOkComposable(
            used = 1234562,
            total = 8929921
        )
    }
}
