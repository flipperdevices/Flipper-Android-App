package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.shared.ButtonElementRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard

@Composable
fun ComposablePairDeviceActionCard(
    modifier: Modifier,
    connectViewModel: ConnectViewModel = viewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceState by deviceStatusViewModel.getState().collectAsState()
    val localDeviceState = deviceState

    InfoElementCard(modifier = modifier) {
        when (localDeviceState) {
            is DeviceStatus.Connected ->
                ComposableDisconnectElement(connectViewModel = connectViewModel)
            DeviceStatus.NoDevice ->
                ComposableFirstConnectElement(connectViewModel = connectViewModel)
            is DeviceStatus.NoDeviceInformation -> if (localDeviceState.connectInProgress) {
                ComposableDisconnectElement(connectViewModel = connectViewModel)
            } else ComposableConnectElement(connectViewModel = connectViewModel)
        }

        if (localDeviceState is DeviceStatus.NoDevice) {
            return@InfoElementCard
        }

        ComposableInfoDivider()

        ButtonElementRow(
            titleId = R.string.info_device_forget,
            iconId = R.drawable.ic_disconnection,
            colorId = DesignSystem.color.red,
            onClick = connectViewModel::showDialogForgetFlipper
        )
    }
}

@Composable
private fun ComposableDisconnectElement(
    modifier: Modifier = Modifier,
    connectViewModel: ConnectViewModel
) {
    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_disconnect,
        iconId = R.drawable.ic_bluetooth_disable,
        colorId = DesignSystem.color.accent_secondary,
        onClick = connectViewModel::onDisconnect
    )
}

@Composable
private fun ComposableConnectElement(
    modifier: Modifier = Modifier,
    connectViewModel: ConnectViewModel
) {
    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_connect,
        iconId = R.drawable.ic_bluetooth,
        colorId = DesignSystem.color.accent_secondary,
        onClick = connectViewModel::connectAndSynchronize
    )
}

@Composable
private fun ComposableFirstConnectElement(
    modifier: Modifier = Modifier,
    connectViewModel: ConnectViewModel
) {
    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_connect,
        iconId = R.drawable.ic_bluetooth,
        colorId = DesignSystem.color.accent_secondary,
        onClick = connectViewModel::goToConnectScreen
    )
}
