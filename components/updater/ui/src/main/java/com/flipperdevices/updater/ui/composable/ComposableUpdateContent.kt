package com.flipperdevices.updater.ui.composable

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.ui.R
import com.flipperdevices.updater.ui.model.UpdaterScreenState

@Composable
fun ComposableUpdateContent(
    updaterScreenState: UpdaterScreenState
) {
    FirmwareVersionText(updaterScreenState.version)

    when (updaterScreenState) {
        UpdaterScreenState.NotStarted -> ComposableInProgressIndicator(
            accentColorId = R.color.update_green,
            secondColorId = R.color.update_green_background,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.CancelingSynchronization -> ComposableInProgressIndicator(
            accentColorId = DesignSystem.color.accent_secondary,
            secondColorId = R.color.update_blue_background,
            iconId = null,
            percent = null
        )
        is UpdaterScreenState.DownloadingFromNetwork -> ComposableInProgressIndicator(
            accentColorId = R.color.update_green,
            secondColorId = R.color.update_green_background,
            iconId = R.drawable.ic_globe,
            percent = updaterScreenState.percent
        )
        is UpdaterScreenState.UploadOnFlipper -> ComposableInProgressIndicator(
            accentColorId = DesignSystem.color.accent_secondary,
            secondColorId = R.color.update_blue_background,
            iconId = R.drawable.ic_bluetooth,
            percent = updaterScreenState.percent
        )
        UpdaterScreenState.CancelingUpdate -> ComposableInProgressIndicator(
            accentColorId = DesignSystem.color.accent_secondary,
            secondColorId = R.color.update_blue_background,
            iconId = null,
            percent = null
        )
        UpdaterScreenState.Rebooting -> ComposableInProgressIndicator(
            accentColorId = DesignSystem.color.accent_secondary,
            secondColorId = R.color.update_blue_background,
            iconId = null,
            percent = null
        )
        UpdaterScreenState.Finish -> return
    }

    DescriptionUpdateText(updaterScreenState)
}

@Composable
private fun FirmwareVersionText(version: FirmwareVersion?) {
    val text = if (version != null) {
        getTextByVersion(version)
    } else ""
    val textColor = if (version != null) {
        colorResource(getColorByChannel(version.channel))
    } else colorResource(DesignSystem.color.black_100)

    Text(
        modifier = Modifier.padding(bottom = 4.dp, start = 24.dp, end = 24.dp),
        text = text,
        color = textColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.W500
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
        UpdaterScreenState.Finish -> R.string.update_stage_update_canceling_desc
        UpdaterScreenState.NotStarted -> R.string.update_stage_starting_desc
        UpdaterScreenState.Rebooting -> R.string.update_stage_rebooting_desc
        is UpdaterScreenState.UploadOnFlipper -> R.string.update_stage_uploading_desc
    }

    Text(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = stringResource(descriptionId) + animatedDots(),
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        color = colorResource(DesignSystem.color.black_30)
    )
}
