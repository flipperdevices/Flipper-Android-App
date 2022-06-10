package com.flipperdevices.info.shared

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@StringRes
fun getNameByChannel(channel: FirmwareChannel): Int {
    return when (channel) {
        FirmwareChannel.DEV -> R.string.info_device_firmware_version_dev
        FirmwareChannel.RELEASE -> R.string.info_device_firmware_version_release
        FirmwareChannel.RELEASE_CANDIDATE -> R.string.info_device_firmware_version_rc
    }
}

@StringRes
fun getFullNameByChannel(channel: FirmwareChannel): Int {
    return when (channel) {
        FirmwareChannel.DEV -> R.string.info_device_firmware_version_dev_full
        FirmwareChannel.RELEASE -> R.string.info_device_firmware_version_release_full
        FirmwareChannel.RELEASE_CANDIDATE -> R.string.info_device_firmware_version_rc_full
    }
}

@Composable
fun getTextByVersion(version: FirmwareVersion): String {
    return "${stringResource(getNameByChannel(version.channel))} ${version.version}"
}

@ColorRes
fun getColorByChannel(channel: FirmwareChannel): Int {
    return when (channel) {
        FirmwareChannel.DEV -> DesignSystem.color.red
        FirmwareChannel.RELEASE_CANDIDATE -> DesignSystem.color.purple
        FirmwareChannel.RELEASE -> R.color.device_info_release
    }
}
