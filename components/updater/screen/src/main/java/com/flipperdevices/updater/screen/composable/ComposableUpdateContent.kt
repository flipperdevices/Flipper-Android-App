package com.flipperdevices.updater.screen.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.FlipperProgressIndicator
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.screen.R
import com.flipperdevices.updater.screen.model.FailedReason
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
@Suppress("LongMethod", "ComplexMethod")
fun ComposableUpdateContent(
    updaterScreenState: UpdaterScreenState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val version = updaterScreenState.updateRequest?.updateTo
    if (version != null) {
        FirmwareVersionText(version)
    } else {
        Spacer(modifier = Modifier.height(29.dp)) // 21 + 8
    }
    val localModifier = modifier
        .padding(vertical = 8.dp, horizontal = 24.dp)
    when (updaterScreenState) {
        UpdaterScreenState.NotStarted -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.updateProgressGreen,
            secondColor = LocalPallet.current.updateProgressBackgroundGreen,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.CancelingSynchronization -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.DownloadingFromNetwork -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.updateProgressGreen,
            secondColor = LocalPallet.current.updateProgressBackgroundGreen,
            iconId = DesignSystem.drawable.ic_globe,
            percent = updaterScreenState.percent
        )
        is UpdaterScreenState.SubGhzProvisioning -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.updateProgressGreen,
            secondColor = LocalPallet.current.updateProgressBackgroundGreen,
            iconId = DesignSystem.drawable.ic_globe,
            percent = null
        )
        is UpdaterScreenState.UploadOnFlipper -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = DesignSystem.drawable.ic_bluetooth,
            percent = updaterScreenState.percent
        )
        UpdaterScreenState.CancelingUpdate -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = null,
            percent = null
        )
        UpdaterScreenState.Rebooting -> FlipperProgressIndicator(
            modifier = localModifier,
            accentColor = LocalPallet.current.accentSecond,
            secondColor = LocalPallet.current.updateProgressBackgroundBlue,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.Failed -> when (updaterScreenState.failedReason) {
            FailedReason.UPLOAD_ON_FLIPPER -> ComposableFailedUploadContent()
            FailedReason.DOWNLOAD_FROM_NETWORK -> ComposableFailedDownloadContent(onRetry)
            FailedReason.OUTDATED_APP -> ComposableOutdatedApp()
            FailedReason.FAILED_SUB_GHZ_PROVISIONING,
            FailedReason.FAILED_INT_STORAGE -> ComposableInternalFlashFailed()
            FailedReason.FAILED_INTERNAL_UPDATE -> ComposableInternalUpdateFailed()
        }
        UpdaterScreenState.Finish -> return
    }

    DescriptionUpdateText(updaterScreenState)
}

@Composable
private fun FirmwareVersionText(version: FirmwareVersion) {
    val text = getTextByVersion(version)
    val textColor = getColorByChannel(version.channel)

    Text(
        modifier = Modifier.heightIn(21.dp),
        text = text,
        color = textColor,
        style = LocalTypography.current.titleM18,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DescriptionUpdateText(
    updaterScreenState: UpdaterScreenState
) {
    val descriptionId = when (updaterScreenState) {
        is UpdaterScreenState.CancelingSynchronization -> R.string.update_stage_sync_canceling_desc
        UpdaterScreenState.CancelingUpdate -> R.string.update_stage_update_canceling_desc
        is UpdaterScreenState.DownloadingFromNetwork -> R.string.update_stage_downloading_desc
        is UpdaterScreenState.SubGhzProvisioning -> R.string.update_stage_subghz_desc
        UpdaterScreenState.Finish -> R.string.update_stage_update_canceling_desc
        UpdaterScreenState.NotStarted -> R.string.update_stage_starting_desc
        UpdaterScreenState.Rebooting -> R.string.update_stage_rebooting_desc
        is UpdaterScreenState.UploadOnFlipper -> R.string.update_stage_uploading_desc
        is UpdaterScreenState.Failed -> return
    }

    Text(
        modifier = Modifier.padding(bottom = 12.dp),
        text = stringResource(descriptionId) + animatedDots(),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text30
    )
}
