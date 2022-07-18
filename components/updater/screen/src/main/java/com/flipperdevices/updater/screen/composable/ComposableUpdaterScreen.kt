package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState

@Composable
fun ComposableUpdaterScreen(
    updaterScreenState: UpdaterScreenState,
    flipperColor: HardwareColor,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Column {
        Column(
            Modifier.weight(weight = 1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpdaterScreenHeader(
                isFailed = updaterScreenState is UpdaterScreenState.Failed,
                flipperColor = flipperColor
            )
            ComposableUpdateContent(updaterScreenState, onRetry)
        }
        CancelButton(updaterScreenState, onCancel)
    }
}

@Composable
private fun UpdaterScreenHeader(
    isFailed: Boolean,
    flipperColor: HardwareColor
) {
    val titleId = if (isFailed) {
        R.string.update_screen_title_failed
    } else R.string.update_screen_title
    val bottomPadding = if (isFailed) 38.dp else 64.dp
    Text(
        modifier = Modifier.padding(top = 48.dp, start = 14.dp, end = 14.dp),
        text = stringResource(titleId),
        style = LocalTypography.current.titleB18,
        textAlign = TextAlign.Center
    )

    val imageId = when (flipperColor) {
        HardwareColor.UNRECOGNIZED,
        HardwareColor.WHITE -> if (isFailed) {
            DesignSystem.drawable.pic_flipper_update_failed
        } else DesignSystem.drawable.pic_flipper_update
        HardwareColor.BLACK -> if (isFailed) {
            DesignSystem.drawable.pic_black_flipper_update_failed
        } else DesignSystem.drawable.pic_black_flipper_update
    }

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, start = 14.dp, end = 14.dp, bottom = bottomPadding),
        painter = painterResource(imageId),
        contentDescription = stringResource(titleId),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun CancelButton(
    updaterScreenState: UpdaterScreenState,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (updaterScreenState == UpdaterScreenState.CancelingUpdate) {
            CircularProgressIndicator(
                color = LocalPallet.current.accentSecond
            )
            return@Box
        }
        Text(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onCancel
            ),
            text = stringResource(R.string.update_screen_cancel),
            textAlign = TextAlign.Center,
            color = LocalPallet.current.accentSecond,
            style = LocalTypography.current.buttonM16
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenNotStartedPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.NotStarted,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenCancelingSynchronizationPreview() {
    val version = FirmwareVersion(
        channel = FirmwareChannel.RELEASE,
        version = "0.65.2"
    )
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.CancelingSynchronization(version = version),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenDownloadingFromNetworkPreview() {
    val version = FirmwareVersion(
        channel = FirmwareChannel.RELEASE,
        version = "0.65.2"
    )
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.DownloadingFromNetwork(
                version = version,
                percent = 0.5f
            ),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenUploadOnFlipperPreview() {
    val version = FirmwareVersion(
        channel = FirmwareChannel.RELEASE,
        version = "0.65.2"
    )
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState =
            UpdaterScreenState.UploadOnFlipper(version = version, percent = 0.5f),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenFailedNetworkPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Failed(FailedReason.DOWNLOAD_FROM_NETWORK),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenFailedOnFlipperPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Failed(FailedReason.UPLOAD_ON_FLIPPER),
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenRebootingPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Rebooting,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenCancelingUpdatePreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.CancelingUpdate,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun ComposableUpdaterScreenFinishPreview() {
    FlipperThemeInternal {
        ComposableUpdaterScreen(
            updaterScreenState = UpdaterScreenState.Finish,
            flipperColor = HardwareColor.BLACK,
            onCancel = {},
            onRetry = {}
        )
    }
}
