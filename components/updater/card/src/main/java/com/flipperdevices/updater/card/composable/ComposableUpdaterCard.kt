package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.card.composable.dialogs.ComposableFailedUpdate
import com.flipperdevices.updater.card.composable.dialogs.ComposableSuccessfulUpdate
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateErrorType
import com.flipperdevices.updater.model.UpdateRequest

@Composable
@Suppress("ModifierReused")
internal fun ComposableUpdaterCardInternal(
    updateStateViewModel: UpdateStateViewModel,
    updateCardViewModel: UpdateCardViewModel,
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
    )
}

@Composable
private fun ComposableUpdaterCard(
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
            is UpdateCardState.Error -> {
                ComposableFirmwareUpdaterError(
                    typeError = cardStateLocal.type,
                    retryUpdate = retryUpdate
                )
            }

            UpdateCardState.InProgress -> ComposableFirmwareUpdaterContent(
                version = null,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest
            )

            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.flipperVersion,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest
            )

            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.update.updateTo,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest
            )

            is UpdateCardState.UpdateFromFile -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.updateVersion,
                updateCardState = cardStateLocal,
                onSelectFirmwareChannel = onSelectChannel,
                onStartUpdateRequest = onStartUpdateRequest
            )
        }
    }
}

@Preview
@Composable
private fun ComposableUpdaterCardPreview() {
    FlipperThemeInternal {
        val lastVersion = FirmwareVersion(
            channel = FirmwareChannel.DEV,
            version = "1.4.5"
        )
        val cardStates = listOf(
            UpdateCardState.InProgress,
            UpdateCardState.NoUpdate(lastVersion),
            UpdateCardState.UpdateAvailable(
                update = UpdateRequest(
                    updateFrom = lastVersion,
                    updateTo = lastVersion,
                    content = OfficialFirmware(DistributionFile(url = "", sha256 = "")),
                    changelog = null
                ),
                isOtherChannel = false
            ),
            UpdateCardState.UpdateAvailable(
                update = UpdateRequest(
                    updateFrom = lastVersion,
                    updateTo = lastVersion,
                    content = OfficialFirmware(DistributionFile(url = "", sha256 = "")),
                    changelog = null
                ),
                isOtherChannel = true
            ),
            UpdateCardState.Error(UpdateErrorType.NO_SD_CARD),
            UpdateCardState.Error(UpdateErrorType.NO_INTERNET),
            UpdateCardState.Error(UpdateErrorType.UNABLE_TO_SERVER)
        )
        Column(Modifier.verticalScroll(rememberScrollState())) {
            cardStates.forEach {
                ComposableUpdaterCard(it)
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}
