package com.flipperdevices.filemanager.ui.components.sdcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
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
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
fun SdCardMissingComposable(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LocalPalletV2.current.surface.contentCard.body.default)
            .padding(12.dp)
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 0.6f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(FR.string.sd_card_missing_title),
                style = LocalTypography.current.titleB18,
                color = LocalPalletV2.current.text.title.primary
            )
            Text(
                text = stringResource(FR.string.sd_card_missing_desc),
                style = LocalTypography.current.bodyM14,
                color = LocalPalletV2.current.text.label.secondary
            )
        }
        Box(
            modifier = Modifier
                .weight(weight = 0.4f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(
                    when {
                        MaterialTheme.colors.isLight -> FR.drawable.ic_sd_card_error_black
                        else -> FR.drawable.ic_sd_card_error_white
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
        SdCardMissingComposable()
    }
}
