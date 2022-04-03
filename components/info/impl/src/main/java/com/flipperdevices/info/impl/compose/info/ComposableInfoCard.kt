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
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel

@Composable
fun ComposableInfoCard(
    modifier: Modifier,
    deviceInfoViewModel: DeviceInfoViewModel = viewModel(),
    deviceViewModel: DeviceViewModel = viewModel(),
    firmwareUpdateViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val deviceInfo by deviceInfoViewModel.getDeviceInfo().collectAsState()
    val deviceInfoRequestStatus by deviceInfoViewModel.getDeviceInfoRequestStatus().collectAsState()
    val deviceStatus by deviceViewModel.getState().collectAsState()
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()

    val firmwareVersionInProgress = deviceStatus is DeviceStatus.Connected
    val isUnsupported = firmwareUpdateStatus is FirmwareUpdateStatus.Unsupported

    InfoElementCard(modifier, R.string.info_device_info_title) {
        ComposableFirmwareVersion(
            deviceInfo.firmwareVersion,
            firmwareVersionInProgress
        )
        ComposableFirmwareBuildDate(
            deviceInfo.firmwareVersion,
            firmwareVersionInProgress
        )
        if (isUnsupported) {
            return@InfoElementCard
        }
        ComposableDeviceInfoRowWithText(
            R.string.info_device_info_int_flash,
            deviceInfoRequestStatus.internalStorageRequestInProgress,
            deviceInfo.flashInt?.toString(LocalContext.current)
        )
        ComposableDeviceInfoRowWithText(
            R.string.info_device_info_ext_flash,
            deviceInfoRequestStatus.externalStorageRequestInProgress,
            deviceInfo.flashSd?.toString(LocalContext.current)
        )
    }
}
