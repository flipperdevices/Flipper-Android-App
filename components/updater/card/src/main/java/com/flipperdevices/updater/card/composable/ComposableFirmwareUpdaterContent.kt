package com.flipperdevices.updater.card.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.updater.card.R
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateErrorType
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableFirmwareUpdaterContent(
    version: FirmwareVersion?,
    updateCardState: UpdateCardState,
    onSelectFirmwareChannel: (FirmwareChannel) -> Unit,
    modifier: Modifier = Modifier,
    onStartUpdateRequest: (UpdateRequest) -> Unit
) {
    val inProgress = version == null
    ComposableDeviceInfoRow(
        titleId = R.string.updater_card_updater_channel,
        inProgress = false,
        content = {
            ComposableUpdaterFirmwareVersionWithChoice(
                modifier = it,
                onSelectFirmwareChannel = onSelectFirmwareChannel,
                version = version
            )
        },
        modifier = modifier
    )
    ComposableInfoDivider()
    ComposableUpdateButton(updateCardState, inProgress, onStartUpdateRequest = onStartUpdateRequest)
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFirmwareUpdaterContentPreview() {
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
                ComposableFirmwareUpdaterContent(
                    lastVersion,
                    updateCardState = it,
                    onSelectFirmwareChannel = {}
                ) {}
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}
