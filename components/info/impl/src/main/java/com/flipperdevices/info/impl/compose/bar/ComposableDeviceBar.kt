package com.flipperdevices.info.impl.compose.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FlipperColorViewModel
import kotlin.math.roundToInt
import com.flipperdevices.core.ui.res.R as DesignSystem

const val FLOAT_TO_PERCENT_QUALIFIER = 100

@Composable
fun ComposableDeviceBar(deviceStatusViewModel: DeviceStatusViewModel = viewModel()) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    DeviceBar(deviceStatus)
}

@Composable
private fun DeviceBar(deviceStatus: DeviceStatus) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlipperImage(deviceStatus)
        FlipperInformation(deviceStatus)
    }
}

@Composable
private fun FlipperInformation(deviceStatus: DeviceStatus) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (deviceStatus) {
            DeviceStatus.NoDevice -> NoDeviceText()
            is DeviceStatus.NoDeviceInformation -> FlipperName(deviceStatus.deviceName)
            is DeviceStatus.Connected -> ConnectedText(deviceStatus)
        }
    }
}

@Composable
private fun FlipperImage(
    deviceStatus: DeviceStatus,
    flipperColorViewModel: FlipperColorViewModel = viewModel()
) {
    val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()
    val disabledFlipperId = when (flipperColor) {
        HardwareColor.UNRECOGNIZED,
        HardwareColor.WHITE -> DesignSystem.drawable.pic_flipper_disabled
        HardwareColor.BLACK -> DesignSystem.drawable.pic_black_flipper_disabled
    }
    val flipperId = when (flipperColor) {
        HardwareColor.UNRECOGNIZED,
        HardwareColor.WHITE -> DesignSystem.drawable.pic_flipper
        HardwareColor.BLACK -> DesignSystem.drawable.pic_black_flipper
    }
    val imageId = when (deviceStatus) {
        DeviceStatus.NoDevice -> disabledFlipperId
        is DeviceStatus.Connected -> flipperId
        is DeviceStatus.NoDeviceInformation -> {
            if (deviceStatus.connectInProgress) {
                disabledFlipperId
            } else {
                flipperId
            }
        }
    }

    val descriptionId = when (deviceStatus) {
        DeviceStatus.NoDevice -> R.string.info_device_no_device
        is DeviceStatus.Connected -> R.string.info_device_connected
        is DeviceStatus.NoDeviceInformation -> R.string.info_device_not_connected
    }

    Image(
        modifier = Modifier.padding(top = 7.dp, bottom = 7.dp, end = 18.dp),
        painter = painterResource(imageId),
        contentDescription = stringResource(descriptionId)
    )
}

@Composable
private fun NoDeviceText() {
    Text(
        text = stringResource(R.string.info_device_no_device),
        style = LocalTypography.current.buttonB16,
        color = LocalPallet.current.onAppBar
    )
}

@Composable
private fun ConnectedText(deviceStatus: DeviceStatus.Connected) {
    val title = deviceStatus.deviceName
    val batteryValue = deviceStatus.batteryLevel
    val isCharging = deviceStatus.isCharging

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlipperName(title)
        if (batteryValue > 0.0f && batteryValue <= 1.0f) {
            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComposableFlipperBattery(
                    percent = batteryValue,
                    isCharging = isCharging,
                    Modifier.size(width = 30.dp, height = 14.dp),
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = "${(batteryValue * FLOAT_TO_PERCENT_QUALIFIER).roundToInt()}%",
                    style = LocalTypography.current.subtitleR12,
                    color = LocalPallet.current.onAppBar
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.FlipperName(title: String) {
    Text(
        modifier = Modifier.padding(bottom = 3.dp),
        text = title,
        style = LocalTypography.current.buttonB16,
        color = LocalPallet.current.onAppBar
    )
    Text(
        text = stringResource(R.string.info_device_model_name),
        style = LocalTypography.current.subtitleR12,
        color = LocalPallet.current.onAppBar
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperDeviceBarInformationPreview() {
    val deviceStatus = setOf(
        DeviceStatus.NoDevice,
        DeviceStatus.Connected(deviceName = "Flipper", batteryLevel = 0.3f, isCharging = false),
        DeviceStatus.Connected(deviceName = "Charge", batteryLevel = 0.7f, isCharging = true),
        DeviceStatus.NoDeviceInformation(deviceName = "No device info", connectInProgress = false),
        DeviceStatus.NoDeviceInformation(deviceName = "Connecting...", connectInProgress = true)
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        deviceStatus.forEach {
            DeviceBar(it)
        }
    }
}
