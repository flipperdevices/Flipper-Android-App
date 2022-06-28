package com.flipperdevices.updater.card.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.composable.dialogs.ComposableFailedUpdate
import com.flipperdevices.updater.card.composable.dialogs.ComposableSuccessfulUpdate
import com.flipperdevices.updater.card.model.FlipperUpdateState
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState

@Composable
internal fun ComposableUpdaterCardInternal(
    modifier: Modifier,
    updateStateViewModel: UpdateStateViewModel = viewModel(),
    updateCardViewModel: UpdateCardViewModel = viewModel()
) {
    val updateState by updateStateViewModel.getUpdateState().collectAsState()
    val localDeviceStatus = updateState
    // Not use when, because internal Jetpack Compose crash. ¯\_(ツ)_/¯
    // https://gist.github.com/LionZXY/609fa537747782d01e8f4cbdcdc882cf
    if (updateState == FlipperUpdateState.Updating) {
        ComposableUpdaterReboot(modifier)
        return
    }

    when (localDeviceStatus) {
        is FlipperUpdateState.Complete -> ComposableSuccessfulUpdate(
            localDeviceStatus.version,
            updateStateViewModel::onDismissUpdateDialog
        )
        is FlipperUpdateState.Failed -> ComposableFailedUpdate(
            localDeviceStatus.version,
            updateStateViewModel::onDismissUpdateDialog
        )
        FlipperUpdateState.Ready -> {}
        FlipperUpdateState.NotReady -> return
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
            UpdateCardState.InProgress -> ComposableFirmwareUpdaterContent(
                version = null,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = updateCardViewModel::onSelectChannel
            )
            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.flipperVersion,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = updateCardViewModel::onSelectChannel
            )
            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.lastVersion,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = updateCardViewModel::onSelectChannel
            )
        }
    }
}

@Composable
private fun ComposableFirmwareUpdaterContent(
    version: FirmwareVersion?,
    updateCardState: UpdateCardState,
    onSelectFirmwareChannel: (FirmwareChannel) -> Unit
) {
    val inProgress = version == null
    ComposableDeviceInfoRow(titleId = R.string.updater_card_updater_channel, inProgress = false) {
        ComposableUpdaterFirmwareVersionWithChoice(
            modifier = it,
            onSelectFirmwareChannel = onSelectFirmwareChannel,
            version = version
        )
    }
    ComposableInfoDivider()
    ComposableUpdateButton(updateCardState, inProgress)
}
