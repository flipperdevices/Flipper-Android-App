package com.flipperdevices.info.impl.compose.elements

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
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.info.ComposableDeviceInfoRow
import com.flipperdevices.info.impl.compose.info.ComposableFirmwareVersionValue
import com.flipperdevices.info.impl.compose.info.ComposableInfoDivider
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateCardState

@Composable
fun ComposableUpdaterCard(
    modifier: Modifier,
    updaterUiApi: UpdaterUIApi,
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()

    if (deviceStatus !is DeviceStatus.Connected) {
        return
    }

    InfoElementCard(
        modifier = modifier,
        titleId = R.string.info_device_updater_title
    ) {
        val cardState by updaterUiApi.getUpdateCardState()
        val cardStateLocal = cardState

        when (cardStateLocal) {
            UpdateCardState.Error -> TODO()
            UpdateCardState.InProgress -> ComposableFirmwareUpdaterInProgress()
            is UpdateCardState.NoUpdate -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.flipperVersion,
                isUpdateAvailable = false
            )
            is UpdateCardState.UpdateAvailable -> ComposableFirmwareUpdaterContent(
                version = cardStateLocal.lastVersion,
                isUpdateAvailable = true
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
    version: FirmwareVersion,
    isUpdateAvailable: Boolean
) {
    ComposableDeviceInfoRow(titleId = R.string.info_device_updater_channel, inProgress = false) {
        ComposableFirmwareVersionValue(modifier = it, version = version)
    }
    ComposableInfoDivider()
}
