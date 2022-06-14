package com.flipperdevices.info.impl.compose.info

import androidx.compose.runtime.Composable
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableDeviceInfoRowText
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
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
        ComposableDeviceInfoRowText(
            modifier = it,
            text = getTextByVersion(firmwareVersion),
            colorId = getColorByChannel(firmwareVersion.channel)
        )
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
