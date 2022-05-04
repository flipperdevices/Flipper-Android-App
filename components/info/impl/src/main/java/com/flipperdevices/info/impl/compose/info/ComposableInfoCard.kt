package com.flipperdevices.info.impl.compose.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.core.ui.composable.LocalRouter
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.elements.InfoElementCard
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.model.toString
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel

@Composable
fun ComposableInfoCard(
    modifier: Modifier,
    deviceStatusViewModel: DeviceStatusViewModel = viewModel(),
    firmwareUpdateViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()
    val isUnsupported = firmwareUpdateStatus is FirmwareUpdateStatus.Unsupported

    InfoElementCard(modifier, R.string.info_device_info_title) {
        ComposableInfoCardContent(isUnsupported)
        if (deviceStatus is DeviceStatus.Connected && !isUnsupported) {
            ComposableFullInfoButton()
        }
    }
}

@Composable
fun ComposableInfoCardContent(
    isUnsupported: Boolean,
    deviceInfoViewModel: DeviceInfoViewModel = viewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceInfo by deviceInfoViewModel.getDeviceInfo().collectAsState()
    val deviceInfoRequestStatus by deviceInfoViewModel.getDeviceInfoRequestStatus().collectAsState()
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()

    val firmwareVersionInProgress = deviceStatus is DeviceStatus.Connected

    ComposableFirmwareVersion(
        deviceInfo.firmwareVersion,
        firmwareVersionInProgress
    )
    ComposableInfoDivider()
    ComposableFirmwareBuildDate(
        deviceInfo.firmwareVersion,
        firmwareVersionInProgress
    )
    if (isUnsupported) {
        return
    }
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_int_flash,
        deviceInfoRequestStatus.internalStorageRequestInProgress,
        deviceInfo.flashInt?.toString(LocalContext.current)
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_ext_flash,
        deviceInfoRequestStatus.externalStorageRequestInProgress,
        deviceInfo.flashSd?.toString(LocalContext.current)
    )
}

@Composable
private fun ComposableFullInfoButton(
    deviceInfoViewModel: DeviceInfoViewModel = viewModel()
) {
    val router = LocalRouter.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(),
                onClick = { deviceInfoViewModel.onOpenFullDeviceInfo(router) },
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(all = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.info_device_info_more_information),
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = colorResource(DesignSystem.color.black_16)
        )
        Icon(
            modifier = Modifier
                .padding(start = 1.dp)
                .size(size = 12.dp),
            painter = painterResource(DesignSystem.drawable.ic_forward),
            contentDescription = stringResource(R.string.info_device_info_more_information),
            tint = colorResource(DesignSystem.color.black_16)
        )
    }
}
