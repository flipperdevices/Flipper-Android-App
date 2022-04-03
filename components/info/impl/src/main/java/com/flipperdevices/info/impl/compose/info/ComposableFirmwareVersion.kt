package com.flipperdevices.info.impl.compose.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.FirmwareVersion

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
private fun ComposableFirmwareVersionValue(
    modifier: Modifier = Modifier,
    version: FirmwareVersion
) {
    val prefixId = when (version) {
        is FirmwareVersion.Dev -> R.string.info_device_firmware_version_dev
        is FirmwareVersion.Release -> R.string.info_device_firmware_version_release
        is FirmwareVersion.ReleaseCandidate -> R.string.info_device_firmware_version_rc
    }

    val versionText = when (version) {
        is FirmwareVersion.Dev -> version.commitSHA
        is FirmwareVersion.Release -> version.version
        is FirmwareVersion.ReleaseCandidate -> version.version
    }

    val colorId = when (version) {
        is FirmwareVersion.Dev -> DesignSystem.color.red
        is FirmwareVersion.ReleaseCandidate -> DesignSystem.color.purple
        is FirmwareVersion.Release -> R.color.device_info_release
    }

    ComposableDeviceInfoRowText(
        modifier,
        text = "${stringResource(prefixId)} $versionText",
        colorId = colorId
    )
}
