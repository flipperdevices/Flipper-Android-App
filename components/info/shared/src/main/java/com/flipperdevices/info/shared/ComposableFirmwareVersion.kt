package com.flipperdevices.info.shared

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPallet
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

@StringRes
fun getDescriptionByChannel(channel: FirmwareChannel): Int {
    return when (channel) {
        FirmwareChannel.DEV -> R.string.info_device_firmware_version_dev_desc
        FirmwareChannel.RELEASE -> R.string.info_device_firmware_version_release_desc
        FirmwareChannel.RELEASE_CANDIDATE -> R.string.info_device_firmware_version_rc_desc
    }
}

@Composable
fun getTextByVersion(version: FirmwareVersion): String {
    return "${stringResource(getNameByChannel(version.channel))} ${version.version}"
}

@Composable
fun getColorByChannel(channel: FirmwareChannel): Color {
    return when (channel) {
        FirmwareChannel.DEV -> LocalPallet.current.redFirmware
        FirmwareChannel.RELEASE_CANDIDATE -> LocalPallet.current.greenFirmware
        FirmwareChannel.RELEASE -> LocalPallet.current.purpleFirmware
    }
}
