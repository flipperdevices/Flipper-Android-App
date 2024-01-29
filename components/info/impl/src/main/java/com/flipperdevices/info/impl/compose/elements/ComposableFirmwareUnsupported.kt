package com.flipperdevices.info.impl.compose.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFirmwareUpdate(
    supportedState: FlipperSupportedState,
    modifier: Modifier = Modifier
) {
    when (supportedState) {
        FlipperSupportedState.DEPRECATED_FLIPPER -> InfoElementCard(
            modifier = modifier,
            titleId = R.string.info_firmware_update_title
        ) {
            ComposableFirmwareUpdateUnsupported(
                imageId = DesignSystem.drawable.ic_firmware_flipper_deprecated,
                titleId = R.string.info_firmware_update_unsupported_title,
                descriptionId = R.string.info_firmware_update_unsupported_desc,
                linkId = R.string.info_firmware_update_unsupported_link
            )
        }
        FlipperSupportedState.DEPRECATED_APPLICATION -> InfoElementCard(
            modifier = modifier,
            titleId = R.string.info_firmware_update_title
        ) {
            ComposableFirmwareUpdateUnsupported(
                imageId = if (MaterialTheme.colors.isLight) {
                    DesignSystem.drawable.ic_firmware_application_deprecated
                } else {
                    DesignSystem.drawable.ic_firmware_application_deprecated_dark
                },
                titleId = R.string.info_firmware_update_application_unsupported_title,
                descriptionId = R.string.info_firmware_update_application_unsupported_desc,
                linkId = R.string.info_firmware_update_application_unsupported_link
            )
        }
        FlipperSupportedState.READY -> {}
    }
}

@Composable
private fun ComposableFirmwareUpdateUnsupported(
    @DrawableRes imageId: Int,
    @StringRes titleId: Int,
    @StringRes descriptionId: Int,
    @StringRes linkId: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(all = 12.dp),
            painter = painterResource(imageId),
            contentDescription = stringResource(titleId)
        )
        Text(
            text = stringResource(titleId),
            style = LocalTypography.current.bodyM14,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp),
            text = stringResource(descriptionId),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text40,
            textAlign = TextAlign.Center
        )

        ClickableUrlText(
            modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
            markdownResId = linkId,
            style = LocalTypography.current.bodyM14.copy(
                color = LocalPallet.current.text60
            )
        )
    }
}
