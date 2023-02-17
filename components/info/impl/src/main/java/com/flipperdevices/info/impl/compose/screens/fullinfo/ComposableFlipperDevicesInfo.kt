package com.flipperdevices.info.impl.compose.screens.fullinfo

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.flipperdevices.bridge.rpcinfo.model.FlipperDeviceInfo
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableDeviceInfoRowText
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableInfoDivider

@Composable
fun ComposableFlipperDevicesInfo(
    info: FlipperDeviceInfo,
    inProgress: Boolean
) {
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_device_name,
        inProgress = inProgress,
        value = info.deviceName
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_hardware_model,
        inProgress = inProgress,
        value = info.hardwareModel
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_hardware_region,
        inProgress = inProgress,
        value = info.hardwareRegion
    )
    ComposableInfoDivider()
    ComposableCountryRow(
        titleId = R.string.full_info_hardware_region_provisioned,
        inProgress = inProgress,
        value = info.hardwareRegionProv
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_hardware_version,
        inProgress = inProgress,
        value = info.hardwareVersion
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_hardware_otp_version,
        inProgress = inProgress,
        value = info.hardwareOTPVersion
    )
    ComposableInfoDivider()
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_serial_number,
        inProgress = inProgress,
        value = info.serialNumber
    )
}

@Composable
private fun ComposableCountryRow(
    titleId: Int,
    value: String?,
    inProgress: Boolean
) {
    if (value == null) {
        ComposableDeviceInfoRow(titleId = titleId, inProgress = inProgress, content = null)
    } else {
        ComposableDeviceInfoRow(
            titleId,
            inProgress,
            content = { modifier ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComposableDeviceInfoRowText(modifier = modifier, text = value)
                }
            }
        )
    }
}
