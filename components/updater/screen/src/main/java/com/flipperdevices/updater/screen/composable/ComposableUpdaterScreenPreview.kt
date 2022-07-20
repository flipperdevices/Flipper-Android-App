package com.flipperdevices.updater.screen.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState

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
