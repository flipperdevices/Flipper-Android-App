package com.flipperdevices.core.ui.errors.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.errors.R
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableFlipperFirmwareNotSupported(
    onOpenDeviceScreen: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(R.string.common_error_firmware_wrong_title),
        style = LocalTypography.current.bodyM14,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    val descPrefix = stringResource(R.string.common_error_firmware_wrong_desc_1)
    val descSelected = stringResource(R.string.common_error_firmware_wrong_desc_2_green)
    val descPostfix = stringResource(R.string.common_error_firmware_wrong_desc_3)
    val descStyle = LocalTypography.current.bodyR14
    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = buildAnnotatedString {
            append(descPrefix)
            append(' ')
            withStyle(
                descStyle.toSpanStyle().copy(
                    color = LocalPallet.current.channelFirmwareRelease
                )
            ) {
                append(descSelected)
            }
            append(' ')
            append(descPostfix)
        },
        style = descStyle,
        color = LocalPallet.current.text40,
        textAlign = TextAlign.Center
    )

    Text(
        modifier = Modifier
            .padding(top = 12.dp)
            .clickable(onClick = onOpenDeviceScreen),
        text = stringResource(R.string.common_error_firmware_wrong_btn),
        style = LocalTypography.current.bodyM14,
        color = LocalPallet.current.accentSecond,
        textAlign = TextAlign.Center
    )
}
