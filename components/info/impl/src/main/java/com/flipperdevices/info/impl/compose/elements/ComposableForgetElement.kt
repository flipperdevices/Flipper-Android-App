package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceViewModel

@Composable
fun ComposableForgetElement(
    modifier: Modifier,
    deviceViewModel: DeviceViewModel = viewModel(),
    connectViewModel: ConnectViewModel = viewModel()
) {
    val deviceState by deviceViewModel.getState().collectAsState()
    if (deviceState is DeviceStatus.NoDevice) {
        return
    }

    if ((deviceState as? DeviceStatus.NoDeviceInformation)?.connectInProgress == true) {
        return
    }

    ButtonElementCard(
        modifier = modifier,
        titleId = R.string.info_device_forget,
        iconId = R.drawable.ic_disconnection,
        colorId = DesignSystem.color.red,
        onClick = connectViewModel::forgetFlipper
    )
}
