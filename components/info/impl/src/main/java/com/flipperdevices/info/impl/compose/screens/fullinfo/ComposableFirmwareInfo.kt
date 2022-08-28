package com.flipperdevices.info.impl.compose.screens.fullinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.flipperdevices.bridge.api.model.FirmwareInfo
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.ComposableLongDeviceInfoRowText
import com.flipperdevices.info.shared.getColorByChannel

@Composable
fun ComposableFirmwareInfo(
    deviceInfoViewModel: DeviceInfoViewModel,
    info: FirmwareInfo,
    inProgress: Boolean
) {
    val firmwareChannel = deviceInfoViewModel.getFirmwareChannel(info.softwareRevision)
    ComposableSoftwareRevision(
        titleId = R.string.full_info_software_revision,
        inProgress = inProgress,
        value = info.softwareRevision,
        color = firmwareChannel?.let { getColorByChannel(it) } ?: Color.Transparent
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_build_date,
        inProgress = inProgress,
        value = info.buildDate
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_target,
        inProgress = inProgress,
        value = info.target
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_protobuf_version,
        inProgress = inProgress,
        value = info.protobufVersion?.toString()
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_device_info_version,
        inProgress = inProgress,
        value = info.deviceInfoVersion?.toString()
    )
}

@Composable
private fun ComposableSoftwareRevision(
    titleId: Int,
    value: String?,
    inProgress: Boolean,
    color: Color
) {
    if (value == null) {
        ComposableDeviceInfoRow(titleId = titleId, inProgress = inProgress, null)
    } else {
        ComposableDeviceInfoRow(titleId, inProgress) { modifier ->
            ComposableLongDeviceInfoRowText(
                modifier = modifier,
                text = value,
                lines = 2,
                color = color
            )
        }
    }
}
