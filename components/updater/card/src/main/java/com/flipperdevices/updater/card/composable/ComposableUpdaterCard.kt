package com.flipperdevices.updater.card.composable

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.model.FlipperState
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState

@Composable
internal fun ComposableUpdaterCardInternal(
    modifier: Modifier,
    deviceStatusViewModel: UpdateStateViewModel = viewModel(),
    updateCardViewModel: UpdateCardViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()


    // Not use when, because internal Jetpack Compose crash. ¯\_(ツ)_/¯
    // https://gist.github.com/LionZXY/609fa537747782d01e8f4cbdcdc882cf
    if (deviceStatus == FlipperState.UPDATING) {
        ComposableUpdaterReboot(modifier)
        return
    }

    when (deviceStatus) {
        FlipperState.COMPLETE -> {}
        FlipperState.FAILED -> {}
        FlipperState.READY -> {}
        FlipperState.NOT_READY -> return
        else -> error("Can't find this device status")
    }

    InfoElementCard(
        modifier = modifier,
        titleId = R.string.updater_card_updater_title
    ) {
        val cardState by updateCardViewModel.getUpdateCardState().collectAsState()
        val cardStateLocal = cardState

        when (cardStateLocal) {
            is UpdateCardState.Error -> {
                ComposableFirmwareUpdaterError(
                    typeError = cardStateLocal.type,
                    retryUpdate = updateCardViewModel::retry
                )
            }
            UpdateCardState.InProgress -> ComposableFirmwareUpdaterInProgress()
            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.flipperVersion,
                updateCardState = cardStateLocal,
                updateCardViewModel = updateCardViewModel
            )
            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.lastVersion,
                updateCardState = cardStateLocal,
                updateCardViewModel = updateCardViewModel
            )
        }
    }
}

@Composable
private fun ComposableFirmwareUpdaterInProgress() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(top = 55.dp)
                .size(24.dp),
            color = colorResource(DesignSystem.color.accent_secondary),
            strokeWidth = 2.dp
        )
        Text(
            modifier = Modifier.padding(top = 14.dp, bottom = 55.dp),
            text = stringResource(R.string.updater_card_updater_progress),
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_30),
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
private fun ComposableFirmwareUpdaterContent(
    version: FirmwareVersion,
    updateCardState: UpdateCardState,
    updateCardViewModel: UpdateCardViewModel
) {
    ComposableDeviceInfoRow(titleId = R.string.updater_card_updater_channel, inProgress = false) {
        ComposableUpdaterFirmwareVersionWithChoice(
            modifier = it,
            updateCardViewModel = updateCardViewModel,
            version = version
        )
    }
    ComposableInfoDivider()
    ComposableUpdateButton(updateCardState)
}
