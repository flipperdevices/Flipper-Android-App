package com.flipperdevices.info.impl.compose.updater

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
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdatingState

@Composable
fun ComposableUpdaterCard(
    modifier: Modifier,
    updaterUiApi: UpdaterUIApi,
    updaterApi: UpdaterApi,
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()

    if (deviceStatus !is DeviceStatus.Connected) {
        val updateStatus by updaterApi.getState().collectAsState()

        if (updateStatus.state == UpdatingState.Rebooting) {
            ComposableUpdaterReboot(modifier)
        }

        return
    }

    InfoElementCard(
        modifier = modifier,
        titleId = R.string.info_device_updater_title
    ) {
        val updateCardApi = updaterUiApi.getUpdateCardApi()
        val cardState by updateCardApi.getUpdateCardState().collectAsState()
        val cardStateLocal = cardState

        when (cardStateLocal) {
            is UpdateCardState.Error -> {
                ComposableFirmwareUpdaterError(
                    typeError = cardStateLocal.type,
                    retryUpdate = updateCardApi::retry
                )
            }
            UpdateCardState.InProgress -> ComposableFirmwareUpdaterInProgress()
            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                updaterUiApi,
                version = cardStateLocal.flipperVersion,
                updateCardState = cardStateLocal
            )
            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                updaterUiApi,
                version = cardStateLocal.lastVersion,
                updateCardState = cardStateLocal
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
            text = stringResource(R.string.info_device_updater_progress),
            fontSize = 14.sp,
            color = colorResource(DesignSystem.color.black_30),
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
private fun ComposableFirmwareUpdaterContent(
    updaterUiApi: UpdaterUIApi,
    version: FirmwareVersion,
    updateCardState: UpdateCardState
) {
    ComposableDeviceInfoRow(titleId = R.string.info_device_updater_channel, inProgress = false) {
        ComposableUpdaterFirmwareVersionWithChoice(
            modifier = it,
            updaterUIApi = updaterUiApi,
            version = version
        )
    }
    ComposableInfoDivider()
    ComposableUpdateButton(updaterUiApi, updateCardState)
}
