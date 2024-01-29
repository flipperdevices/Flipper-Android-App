package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.shared.ButtonElementRow
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableConnectedDeviceActionCard(
    deviceStatus: DeviceStatus,
    supportedState: FlipperSupportedState,
    requestSynchronize: () -> Unit,
    alarmOnFlipper: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (deviceStatus is DeviceStatus.NoDevice) {
        return
    }

    val enabled = deviceStatus is DeviceStatus.Connected &&
        supportedState == FlipperSupportedState.READY

    InfoElementCard(modifier = modifier) {
        ComposableSynchronize(
            enabled = enabled,
            requestSynchronize = requestSynchronize
        )
        ComposableInfoDivider()
        ComposableAlarmElement(
            enabled = enabled,
            alarmOnFlipper = alarmOnFlipper
        )
    }
}

@Composable
private fun ComposableSynchronize(
    requestSynchronize: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
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
            requestSynchronize
        } else {
            null
        }
    )
}

@Composable
private fun ComposableAlarmElement(
    enabled: Boolean,
    alarmOnFlipper: () -> Unit,
    modifier: Modifier = Modifier
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
            alarmOnFlipper
        } else {
            null
        }
    )
}
