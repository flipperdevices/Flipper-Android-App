package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.info.ComposableFirmwareBuildDate
import com.flipperdevices.info.impl.compose.info.ComposableFirmwareVersion
import com.flipperdevices.info.impl.model.DeviceInfo
import com.flipperdevices.info.impl.model.DeviceInfoRequestStatus
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.toString
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun ComposableInfoCardContent(
    isUnsupported: Boolean,
    deviceInfoViewModel: DeviceInfoViewModel = viewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceInfo by deviceInfoViewModel.getDeviceInfo().collectAsState()
    val deviceInfoRequestStatus by deviceInfoViewModel.getDeviceInfoRequestStatus().collectAsState()
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()

    ComposableInfoCardContentInternal(
        isUnsupported = isUnsupported,
        deviceInfo = deviceInfo,
        deviceStatus = deviceStatus,
        deviceInfoRequestStatus = deviceInfoRequestStatus
    )
}

@Composable
fun ComposableInfoCardContentInternal(
    isUnsupported: Boolean,
    deviceInfo: DeviceInfo,
    deviceStatus: DeviceStatus,
    deviceInfoRequestStatus: DeviceInfoRequestStatus
) {
    val firmwareVersionInProgress = if (deviceStatus is DeviceStatus.NoDeviceInformation) {
        deviceStatus.connectInProgress
    } else deviceStatus is DeviceStatus.Connected

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
        firmwareVersionInProgress || deviceInfoRequestStatus.internalStorageRequestInProgress,
        deviceInfo.flashInt?.toString(LocalContext.current)
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_ext_flash,
        firmwareVersionInProgress || deviceInfoRequestStatus.externalStorageRequestInProgress,
        deviceInfo.flashSd?.toString(LocalContext.current)
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableInfoCardContentInternalPreview() {
    val deviceInfo = DeviceInfo(
        firmwareVersion = FirmwareVersion(
            channel = FirmwareChannel.UNKNOWN,
            version = "fsfsfmfmmflmsmflslmfmlsfmlsmlfsmfmlsmlfslmfmlslfmlsmflsmlflslfmlsflmslmfslf"
        ),
        flashInt = StorageStats.Loaded(
            total = 1000,
            free = 300
        ),
        flashSd = StorageStats.Error
    )
    val deviceStatus = DeviceStatus.Connected(
        deviceName = "Flipper",
        batteryLevel = 0.4f,
        isCharging = true
    )
    val deviceInfoRequestStatus = DeviceInfoRequestStatus()
    FlipperThemeInternal {
        InfoElementCard(Modifier, R.string.info_device_info_title) {
            ComposableInfoCardContentInternal(
                isUnsupported = false,
                deviceInfo = deviceInfo,
                deviceStatus = deviceStatus,
                deviceInfoRequestStatus = deviceInfoRequestStatus
            )
        }
    }
}
