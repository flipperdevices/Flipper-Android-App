package com.flipperdevices.updater.card.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.composable.dialogs.ComposableFailedUpdate
import com.flipperdevices.updater.card.composable.dialogs.ComposableSuccessfulUpdate
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest

@Composable
@Suppress("ModifierReused")
internal fun ComposableUpdaterCardInternal(
    updateStateViewModel: UpdateStateViewModel,
    updateCardViewModel: UpdateCardViewModel,
    updateRequestViewModel: UpdateRequestViewModel,
    modifier: Modifier = Modifier,
    onStartUpdateRequest: (UpdateRequest) -> Unit = {},
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

        FlipperUpdateState.Ready,
        FlipperUpdateState.ConnectingInProgress -> {
        }

        FlipperUpdateState.NotConnected -> return
        else -> error("Can't find this device status")
    }

    val cardState by updateCardViewModel.getUpdateCardState().collectAsState()
    ComposableUpdaterCard(
        modifier = modifier,
        cardStateLocal = cardState,
        onSelectChannel = updateCardViewModel::onSelectChannel,
        retryUpdate = updateCardViewModel::refresh,
        onStartUpdateRequest = onStartUpdateRequest,
        updateRequestViewModel = updateRequestViewModel
    )
}

@Composable
private fun ComposableUpdaterCard(
    updateRequestViewModel: UpdateRequestViewModel,
    cardStateLocal: UpdateCardState,
    modifier: Modifier = Modifier,
    onSelectChannel: (FirmwareChannel) -> Unit = {},
    onStartUpdateRequest: (UpdateRequest) -> Unit = {},
    retryUpdate: () -> Unit = {}
) {
    InfoElementCard(
        modifier = modifier,
        titleId = R.string.updater_card_updater_title
    ) {
        when (cardStateLocal) {
            is UpdateCardState.Error -> ComposableFirmwareUpdaterError(
                typeError = cardStateLocal.type,
                retryUpdate = retryUpdate
            )

            UpdateCardState.InProgress -> ComposableFirmwareUpdaterContent(
                version = null,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest,
                updateRequestViewModel = updateRequestViewModel

            )

            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.flipperVersion,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest,
                updateRequestViewModel = updateRequestViewModel
            )

            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.update.updateTo,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest,
                updateRequestViewModel = updateRequestViewModel
            )

            is UpdateCardState.UpdateFromFile -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.updateVersion,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest,
                updateRequestViewModel = updateRequestViewModel
            )
        }
    }
}
