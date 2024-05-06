package com.flipperdevices.info.impl.compose.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableLongDeviceInfoRowText
import com.flipperdevices.info.shared.getColorByChannel
import com.flipperdevices.info.shared.getTextByVersion
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion

@Composable
fun ComposableFirmwareVersion(
    firmwareVersion: FirmwareVersion?,
    firmwareVersionInProgress: Boolean,
    modifier: Modifier = Modifier,
) {
    if (firmwareVersion == null) {
        ComposableDeviceInfoRow(
            R.string.info_device_info_version,
            firmwareVersionInProgress,
            modifier,
            null
        )
        return
    }
    ComposableDeviceInfoRow(
        R.string.info_device_info_version,
        firmwareVersionInProgress,
        content = {
            ComposableLongDeviceInfoRowText(
                modifier = it,
                text = getTextByVersion(firmwareVersion),
                color = getColorByChannel(firmwareVersion.channel)
            )
        }
    )
}

@Composable
fun ComposableFirmwareBuildDate(
    firmwareVersion: FirmwareVersion?,
    firmwareVersionInProgress: Boolean,
    modifier: Modifier = Modifier,
) {
    if (firmwareVersion == null) {
        ComposableDeviceInfoRow(
            R.string.info_device_info_build_date,
            firmwareVersionInProgress,
            modifier,
            null,
        )
        return
    }

    ComposableDeviceInfoRowWithText(
        R.string.info_device_info_build_date,
        firmwareVersionInProgress,
        firmwareVersion.buildDate
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFirmwareVersionAndBuildDatePreview() {
    val firmwareVersion = FirmwareVersion(
        channel = FirmwareChannel.DEV,
        version = "1.1.1",
        buildDate = "today"
    )
    Column(
        Modifier
            .width(300.dp)
            .background(LocalPallet.current.background)
    ) {
        ComposableFirmwareVersion(firmwareVersion, true)
        ComposableFirmwareBuildDate(firmwareVersion, true)

        ComposableFirmwareVersion(null, true)
        ComposableFirmwareBuildDate(null, true)

        ComposableFirmwareVersion(null, false)
        ComposableFirmwareBuildDate(null, false)
    }
}
