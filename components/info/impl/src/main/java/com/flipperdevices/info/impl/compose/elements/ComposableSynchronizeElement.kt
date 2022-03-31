package com.flipperdevices.info.impl.compose.elements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.ConnectRequestState
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel

const val ROTATION_DURATION = 3000

@Composable
fun ComposableSynchronizeElement(
    modifier: Modifier,
    deviceViewModel: DeviceViewModel = viewModel(),
    connectViewModel: ConnectViewModel = viewModel()
) {
    val deviceState by deviceViewModel.getState().collectAsState()
    val localDeviceState = deviceState
    when (localDeviceState) {
        DeviceStatus.NoDevice -> ComposableConnect(modifier, connectViewModel)
        is DeviceStatus.NoDeviceInformation -> {
            if (localDeviceState.connectInProgress) {
                ComposableSynchronize(modifier, connectViewModel, enabled = false)
            } else ComposableConnectAndSynchronize(modifier, connectViewModel)
        }
        is DeviceStatus.Connected -> ComposableSynchronize(modifier, connectViewModel)
    }
}

@Composable
private fun ComposableConnect(
    modifier: Modifier,
    connectViewModel: ConnectViewModel
) {
    ButtonElementCard(
        modifier = modifier,
        titleId = R.string.info_device_connect,
        iconId = R.drawable.ic_bluetooth,
        colorId = DesignSystem.color.accent_secondary,
        onClick = connectViewModel::goToConnectScreen
    )
}

@Composable
private fun ComposableConnectAndSynchronize(
    modifier: Modifier,
    connectViewModel: ConnectViewModel
) {
    val connectRequestState by connectViewModel.getConnectRequestState().collectAsState()

    var angel = 0f
    if (connectRequestState == ConnectRequestState.CONNECTING_AND_SYNCHRONIZING) {
        val infiniteTransition = rememberInfiniteTransition()
        val angleRotation by infiniteTransition.animateFloat(
            initialValue = 0F,
            targetValue = 360F,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = ROTATION_DURATION, easing = LinearEasing)
            )
        )
        angel = angleRotation
    }

    ButtonElementCard(
        modifier = modifier,
        iconAngel = angel,
        titleId = R.string.info_device_connect_and_synchronize,
        iconId = R.drawable.ic_syncing,
        colorId = DesignSystem.color.accent_secondary,
        onClick = connectViewModel::connectAndSynchronize
    )
}

@Composable
private fun ComposableSynchronize(
    modifier: Modifier,
    connectViewModel: ConnectViewModel,
    enabled: Boolean = true,
    firmwareUpdateViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()
    val isUnsupported = firmwareUpdateStatus is FirmwareUpdateStatus.Unsupported || !enabled

    val colorId = if (isUnsupported) {
        DesignSystem.color.black_16
    } else DesignSystem.color.accent_secondary

    ButtonElementCard(
        modifier = modifier,
        titleId = R.string.info_device_synchronize,
        iconId = R.drawable.ic_syncing,
        colorId = colorId,
        onClick = if (isUnsupported) {
            null
        } else connectViewModel::requestSynchronize
    )
}
