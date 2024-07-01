package com.flipperdevices.changelog.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.changelog.impl.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.keyscreen.shared.bar.ComposableBarCancelIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun ChangelogScreenBarComposable(
    firmwareVersion: FirmwareVersion,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    ComposableKeyScreenAppBar(
        modifier = modifier,
        centerBlock = {
            Column(
                modifier = it,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.whatsnew_title),
                    color = LocalPalletV2.current.text.title.primary,
                    style = LocalTypography.current.titleEB18
                )
                Text(
                    text = getTextByVersion(firmwareVersion),
                    color = getColorByChannel(firmwareVersion.channel),
                    style = LocalTypography.current.subtitleM12
                )
            }
        },
        endBlock = {
            ComposableBarCancelIcon(
                modifier = it,
                onClick = onBack,
            )
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ChangelogScreenBarComposablePreview() {
    FlipperThemeInternal {
        ChangelogScreenBarComposable(
            firmwareVersion = FirmwareVersion(
                channel = FirmwareChannel.DEV,
                version = "0.99"
            ),
            onBack = {}
        )
    }
}
