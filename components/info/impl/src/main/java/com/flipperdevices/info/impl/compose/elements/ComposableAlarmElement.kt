package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel

@Composable
fun ComposableAlarmElement(
    modifier: Modifier,
    alarmViewModel: AlarmViewModel = viewModel(),
    deviceViewModel: DeviceViewModel = viewModel(),
    firmwareUpdateViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val deviceState by deviceViewModel.getState().collectAsState()
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()
    val isUnsupported = firmwareUpdateStatus is FirmwareUpdateStatus.Unsupported

    if (deviceState !is DeviceStatus.Connected) {
        return
    }

    val colorId = if (isUnsupported) {
        DesignSystem.color.black_16
    } else {
        DesignSystem.color.accent_secondary
    }

    ButtonElementCard(
        modifier = modifier,
        titleId = R.string.info_device_play_alert,
        iconId = R.drawable.ic_ring,
        colorId = colorId,
        onClick = if (isUnsupported) {
            null
        } else alarmViewModel::alarmOnFlipper
    )
}
