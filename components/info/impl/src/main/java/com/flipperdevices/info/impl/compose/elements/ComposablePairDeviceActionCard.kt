package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.dialogs.ComposableForgotDialog
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.shared.ButtonElementRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposablePairDeviceActionCard(
    modifier: Modifier = Modifier,
    connectViewModel: ConnectViewModel = tangleViewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = tangleViewModel()
) {
    val navController = LocalGlobalNavigationNavStack.current
    val deviceState by deviceStatusViewModel.getState().collectAsState()
    val localDeviceState = deviceState

    InfoElementCard(modifier = modifier) {
        when (localDeviceState) {
            is DeviceStatus.Connected ->
                ComposableDisconnectElement(onDisconnect = connectViewModel::onDisconnect)
            DeviceStatus.NoDevice ->
                ComposableFirstConnectElement(
                    onGoToConnectScreen = { connectViewModel.goToConnectScreen(navController) }
                )
            is DeviceStatus.NoDeviceInformation -> if (localDeviceState.connectInProgress) {
                ComposableDisconnectElement(onDisconnect = connectViewModel::onDisconnect)
            } else {
                ComposableConnectElement(
                    onConnectAndSynchronize = connectViewModel::connectAndSynchronize
                )
            }
        }

        if (localDeviceState is DeviceStatus.NoDevice) {
            return@InfoElementCard
        }

        ComposableInfoDivider()

        var isForgotDialogOpen by remember { mutableStateOf(false) }
        ButtonElementRow(
            titleId = R.string.info_device_forget,
            iconId = R.drawable.ic_disconnection,
            color = LocalPallet.current.forgetFlipper,
            onClick = { isForgotDialogOpen = true }
        )
        if (isForgotDialogOpen) {
            ComposableForgotDialog(
                flipperName = deviceState.getFlipperName(),
                onCancel = { isForgotDialogOpen = false },
                onForget = connectViewModel::forgetFlipper
            )
        }
    }
}

@Composable
private fun ComposableDisconnectElement(
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_disconnect,
        iconId = DesignSystem.drawable.ic_bluetooth_disable,
        color = LocalPallet.current.accentSecond,
        onClick = onDisconnect
    )
}

@Composable
private fun ComposableConnectElement(
    onConnectAndSynchronize: () -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_connect,
        iconId = DesignSystem.drawable.ic_bluetooth,
        color = LocalPallet.current.accentSecond,
        onClick = onConnectAndSynchronize
    )
}

@Composable
private fun ComposableFirstConnectElement(
    onGoToConnectScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonElementRow(
        modifier = modifier,
        titleId = R.string.info_device_connect,
        iconId = DesignSystem.drawable.ic_bluetooth,
        color = LocalPallet.current.accentSecond,
        onClick = onGoToConnectScreen
    )
}
