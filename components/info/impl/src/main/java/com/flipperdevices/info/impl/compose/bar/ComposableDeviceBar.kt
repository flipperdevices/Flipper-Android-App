package com.flipperdevices.info.impl.compose.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.flippermockup.ComposableFlipperMockup
import com.flipperdevices.core.ui.flippermockup.ComposableFlipperMockupImage
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import kotlin.math.roundToInt

const val FLOAT_TO_PERCENT_QUALIFIER = 100

@Composable
fun ComposableDeviceBar(deviceStatus: DeviceStatus, hardwareColor: HardwareColor) {
    DeviceBar(deviceStatus, hardwareColor)
}

@Composable
private fun DeviceBar(
    deviceStatus: DeviceStatus,
    hardwareColor: HardwareColor
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlipperImage(
            deviceStatus = deviceStatus,
            flipperColor = hardwareColor
        )
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
    flipperColor: HardwareColor,
) {
    val isActive = when (deviceStatus) {
        DeviceStatus.NoDevice -> false
        is DeviceStatus.Connected -> true
        is DeviceStatus.NoDeviceInformation -> !deviceStatus.connectInProgress
    }
    ComposableFlipperMockup(
        flipperColor = flipperColor,
        isActive = isActive,
        mockupImage = ComposableFlipperMockupImage.DEFAULT,
        modifier = Modifier
            .height(100.dp)
            .padding(top = 7.dp, bottom = 7.dp, end = 18.dp)
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
            DeviceBar(it, HardwareColor.BLACK)
        }
    }
}
