package com.flipperdevices.info.impl.compose.screens.fullinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.model.FlipperDeviceInfo
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
        ComposableDeviceInfoRow(titleId = titleId, inProgress = inProgress, null)
    } else {
        ComposableDeviceInfoRow(titleId, inProgress) { modifier ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value == "RU") {
                    Image(
                        modifier = Modifier.height(height = 16.dp)
                            .padding(horizontal = 6.dp),
                        painter = painterResource(
                            com.flipperdevices.core.ui.res.R.drawable.rus_new_flag
                        ),
                        contentDescription = stringResource(
                            R.string.full_info_hardware_region_provisioned_ru
                        )
                    )
                }
                ComposableDeviceInfoRowText(modifier = modifier, text = value)
            }
        }
    }
}
