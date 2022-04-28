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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.drawable.R as ExternalDrawable
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import kotlin.math.roundToInt

const val FLOAT_TO_PERCENT_QUALIFIER = 100

@Composable
fun ComposableDeviceBar(deviceStatusViewModel: DeviceStatusViewModel = viewModel()) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(DesignSystem.color.accent)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlipperImage(deviceStatus)
        val localDeviceStatus = deviceStatus
        when (localDeviceStatus) {
            DeviceStatus.NoDevice -> NoDeviceText()
            is DeviceStatus.NoDeviceInformation -> NotConnectedText(localDeviceStatus.deviceName)
            is DeviceStatus.Connected -> ConnectedText(
                localDeviceStatus.deviceName,
                localDeviceStatus.batteryLevel
            )
        }
    }
}

@Composable
private fun FlipperImage(deviceStatus: DeviceStatus) {
    val imageId = when (deviceStatus) {
        DeviceStatus.NoDevice -> ExternalDrawable.drawable.ic_grey_flipper
        is DeviceStatus.Connected,
        is DeviceStatus.NoDeviceInformation -> ExternalDrawable.drawable.ic_white_flipper
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
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        color = colorResource(DesignSystem.color.black_100)
    )
}

@Composable
private fun NotConnectedText(title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlipperName(title)
    }
}

@Composable
private fun ConnectedText(title: String, batteryValue: Float) {
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
                    Modifier.size(width = 30.dp, height = 14.dp),
                    percent = batteryValue
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = "${(batteryValue * FLOAT_TO_PERCENT_QUALIFIER).roundToInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = colorResource(DesignSystem.color.black_100)
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
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        color = colorResource(DesignSystem.color.black_100)
    )
    Text(
        text = stringResource(R.string.info_device_model_name),
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        color = colorResource(DesignSystem.color.black_100)
    )
}
