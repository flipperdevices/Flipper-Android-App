package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.flipperdevices.info.shared.ButtonElementRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableConnectedDeviceActionCard(
    modifier: Modifier = Modifier,
    deviceStatusViewModel: DeviceStatusViewModel = tangleViewModel(),
    firmwareUpdateViewModel: FirmwareUpdateViewModel = tangleViewModel()
) {
    val deviceState by deviceStatusViewModel.getState().collectAsState()
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()
    if (deviceState is DeviceStatus.NoDevice) {
        return
    }

    val enabled = deviceState is DeviceStatus.Connected &&
        firmwareUpdateStatus == FlipperSupportedState.READY

    InfoElementCard(modifier = modifier) {
        ComposableSynchronize(enabled = enabled)
        ComposableInfoDivider()
        ComposableAlarmElement(enabled = enabled)
    }
}

@Composable
private fun ComposableSynchronize(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    connectViewModel: ConnectViewModel = tangleViewModel()
) {
    val color = if (enabled) {
        LocalPallet.current.accentSecond
    } else {
        LocalPallet.current.text16
    }

    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_synchronize,
        iconId = DesignSystem.drawable.ic_syncing,
        color = color,
        onClick = if (enabled) {
            connectViewModel::requestSynchronize
        } else {
            null
        }
    )
}

@Composable
private fun ComposableAlarmElement(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    alarmViewModel: AlarmViewModel = tangleViewModel()
) {
    val colorId = if (enabled) {
        LocalPallet.current.accentSecond
    } else {
        LocalPallet.current.text16
    }

    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_play_alert,
        iconId = R.drawable.ic_ring,
        color = colorId,
        onClick = if (enabled) {
            alarmViewModel::alarmOnFlipper
        } else {
            null
        }
    )
}
