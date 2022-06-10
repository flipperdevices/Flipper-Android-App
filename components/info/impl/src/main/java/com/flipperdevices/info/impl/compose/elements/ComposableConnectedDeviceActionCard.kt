package com.flipperdevices.info.impl.compose.elements

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.info.ComposableInfoDivider
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel

@Composable
fun ComposableConnectedDeviceActionCard(
    modifier: Modifier,
    deviceStatusViewModel: DeviceStatusViewModel = viewModel(),
    firmwareUpdateViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val deviceState by deviceStatusViewModel.getState().collectAsState()
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()
    if (deviceState is DeviceStatus.NoDevice) {
        return
    }

    val enabled = deviceState is DeviceStatus.Connected &&
        firmwareUpdateStatus is FirmwareUpdateStatus.UpToDate

    InfoElementCard(modifier = modifier) {
        ComposableSynchronize(enabled = enabled)
        ComposableInfoDivider()
        ComposableAlarmElement(enabled = enabled)
    }
}

@Composable
private fun ComposableSynchronize(
    modifier: Modifier = Modifier,
    connectViewModel: ConnectViewModel = viewModel(),
    enabled: Boolean
) {
    val colorId = if (enabled) {
        DesignSystem.color.accent_secondary
    } else DesignSystem.color.black_16

    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_synchronize,
        iconId = R.drawable.ic_syncing,
        colorId = colorId,
        onClick = if (enabled) {
            connectViewModel::requestSynchronize
        } else null
    )
}

@Composable
private fun ComposableAlarmElement(
    modifier: Modifier = Modifier,
    alarmViewModel: AlarmViewModel = viewModel(),
    enabled: Boolean
) {
    val colorId = if (enabled) {
        DesignSystem.color.accent_secondary
    } else DesignSystem.color.black_16

    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_play_alert,
        iconId = R.drawable.ic_ring,
        colorId = colorId,
        onClick = if (enabled) {
            alarmViewModel::alarmOnFlipper
        } else null
    )
}
