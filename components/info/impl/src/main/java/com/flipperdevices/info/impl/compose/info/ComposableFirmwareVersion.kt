package com.flipperdevices.info.impl.compose.info

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun ComposableFirmwareVersion(
    firmwareVersion: FirmwareVersion?,
    firmwareVersionInProgress: Boolean
) {
    if (firmwareVersion == null) {
        ComposableDeviceInfoRow(
            R.string.info_device_info_version,
            firmwareVersionInProgress,
            null
        )
        return
    }
    ComposableDeviceInfoRow(
        R.string.info_device_info_version,
        firmwareVersionInProgress
    ) {
        ComposableFirmwareVersionValue(it, firmwareVersion)
    }
}

@Composable
fun ComposableFirmwareBuildDate(
    firmwareVersion: FirmwareVersion?,
    firmwareVersionInProgress: Boolean
) {
    if (firmwareVersion == null) {
        ComposableDeviceInfoRow(
            R.string.info_device_info_build_date,
            firmwareVersionInProgress,
            null
        )
        return
    }

    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_build_date,
        firmwareVersionInProgress,
        firmwareVersion.buildDate
    )
}

@Composable
fun ComposableFirmwareVersionValue(
    modifier: Modifier = Modifier,
    version: FirmwareVersion
) {
    val prefixId = getNameByChannel(version.channel)

    val colorId = when (version.channel) {
        FirmwareChannel.DEV -> DesignSystem.color.red
        FirmwareChannel.RELEASE_CANDIDATE -> DesignSystem.color.purple
        FirmwareChannel.RELEASE -> R.color.device_info_release
    }

    ComposableDeviceInfoRowText(
        modifier,
        text = "${stringResource(prefixId)} ${version.version}",
        colorId = colorId
    )
}

@StringRes
fun getNameByChannel(channel: FirmwareChannel): Int {
    return when (channel) {
        FirmwareChannel.DEV -> R.string.info_device_firmware_version_dev
        FirmwareChannel.RELEASE -> R.string.info_device_firmware_version_release
        FirmwareChannel.RELEASE_CANDIDATE -> R.string.info_device_firmware_version_rc
    }
}
