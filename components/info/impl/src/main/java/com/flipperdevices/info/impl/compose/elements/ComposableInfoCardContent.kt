package com.flipperdevices.info.impl.compose.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.api.model.FlipperInformationStatus
import com.flipperdevices.info.api.model.FlipperStorageInformation
import com.flipperdevices.info.api.model.StorageStats
import com.flipperdevices.info.api.model.dataOrNull
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.info.ComposableFirmwareBuildDate
import com.flipperdevices.info.impl.compose.info.ComposableFirmwareVersion
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.FlipperBasicInfo
import com.flipperdevices.info.impl.model.deviceinfo.externalStorageRequestInProgress
import com.flipperdevices.info.impl.model.deviceinfo.flashIntStats
import com.flipperdevices.info.impl.model.deviceinfo.flashSdStats
import com.flipperdevices.info.impl.model.deviceinfo.internalStorageRequestInProgress
import com.flipperdevices.info.impl.model.deviceinfo.isExtStorageEnding
import com.flipperdevices.info.impl.model.deviceinfo.isIntStorageEnding
import com.flipperdevices.info.impl.model.deviceinfo.toString
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.DeviceInfoViewModel
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableInfoCardContent(
    isUnsupported: Boolean,
    deviceInfoViewModel: DeviceInfoViewModel = tangleViewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = tangleViewModel()
) {
    val deviceInfo by deviceInfoViewModel.getDeviceInfo().collectAsState()
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()

    ComposableInfoCardContentInternal(
        isUnsupported = isUnsupported,
        flipperBasicInfo = deviceInfo,
        deviceStatus = deviceStatus,
    )
}

@Composable
private fun ComposableInfoCardContentInternal(
    isUnsupported: Boolean,
    flipperBasicInfo: FlipperBasicInfo,
    deviceStatus: DeviceStatus
) {
    val firmwareVersionInProgress = if (deviceStatus is DeviceStatus.NoDeviceInformation) {
        deviceStatus.connectInProgress
    } else {
        deviceStatus is DeviceStatus.Connected &&
            flipperBasicInfo.firmwareVersion !is FlipperInformationStatus.Ready
    }

    ComposableFirmwareVersion(
        flipperBasicInfo.firmwareVersion.dataOrNull(),
        firmwareVersionInProgress
    )
    ComposableInfoDivider()
    ComposableFirmwareBuildDate(
        flipperBasicInfo.firmwareVersion.dataOrNull(),
        firmwareVersionInProgress
    )
    if (isUnsupported) {
        return
    }
    ComposableInfoDivider()
    val storageInfo = flipperBasicInfo.storageInfo
    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_int_flash,
        firmwareVersionInProgress || storageInfo.internalStorageRequestInProgress,
        storageInfo.flashIntStats?.toString(LocalContext.current),
        color = if (storageInfo.isIntStorageEnding()) LocalPallet.current.warningColor else null
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_ext_flash,
        firmwareVersionInProgress || storageInfo.externalStorageRequestInProgress,
        storageInfo.flashSdStats?.toString(LocalContext.current),
        color = if (storageInfo.isExtStorageEnding()) LocalPallet.current.warningColor else null
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableInfoCardContentInternalPreview() {
    val flipperBasicInfo = FlipperBasicInfo(
        firmwareVersion = FlipperInformationStatus.Ready(
            FirmwareVersion(
                channel = FirmwareChannel.UNKNOWN,
                version = "fsfsfmfmmflmsmflslmfmlsfmlsmlfsmfmlsmlfslmfmlslfmlsmflsmlflslfmlsflmslmfslf"
            )
        ),
        storageInfo = FlipperStorageInformation(
            internalStorageStatus = FlipperInformationStatus.Ready(
                StorageStats.Loaded(
                    total = 1000,
                    free = 300
                )
            ),
            externalStorageStatus = FlipperInformationStatus.Ready(StorageStats.Error)
        )
    )
    val deviceStatus = DeviceStatus.Connected(
        deviceName = "Flipper",
        batteryLevel = 0.4f,
        isCharging = true
    )
    FlipperThemeInternal {
        InfoElementCard(Modifier, R.string.info_device_info_title) {
            ComposableInfoCardContentInternal(
                isUnsupported = false,
                flipperBasicInfo = flipperBasicInfo,
                deviceStatus = deviceStatus
            )
        }
    }
}
