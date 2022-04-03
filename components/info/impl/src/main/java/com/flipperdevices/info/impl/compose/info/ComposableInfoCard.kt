package com.flipperdevices.info.impl.compose.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.elements.InfoElementCard
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceViewModel

@Composable
fun ComposableInfoCard(
    modifier: Modifier,
    deviceInfoViewModel: DeviceInfoViewModel = viewModel(),
    deviceViewModel: DeviceViewModel = viewModel()
) {
    val deviceInfo by deviceInfoViewModel.getDeviceInfo().collectAsState()
    val deviceInfoRequestStatus by deviceInfoViewModel.getDeviceInfoRequestStatus().collectAsState()

    val deviceStatus by deviceViewModel.getState().collectAsState()
    val firmwareVersionInProgress = deviceStatus is DeviceStatus.Connected

    InfoElementCard(modifier, R.string.info_device_info_title) {
        ComposableFirmwareVersion(
            deviceInfo.firmwareVersion,
            firmwareVersionInProgress
        )
        ComposableFirmwareBuildDate(
            deviceInfo.firmwareVersion,
            firmwareVersionInProgress
        )
        ComposableDeviceInfoRowWithText(
            R.string.info_device_info_int_flash,
            deviceInfoRequestStatus.internalStorageRequestFinished,
            deviceInfo.flashInt?.toString(LocalContext.current)
        )
        ComposableDeviceInfoRowWithText(
            R.string.info_device_info_ext_flash,
            deviceInfoRequestStatus.externalStorageRequestFinished,
            deviceInfo.flashSd?.toString(LocalContext.current)
        )
    }
}
