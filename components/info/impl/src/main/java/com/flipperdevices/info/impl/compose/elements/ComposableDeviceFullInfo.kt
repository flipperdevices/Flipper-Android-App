package com.flipperdevices.info.impl.compose.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.isNotNull
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceFullInfo
import com.flipperdevices.info.impl.model.FirmwareInfo
import com.flipperdevices.info.impl.model.FlipperDeviceInfo
import com.flipperdevices.info.impl.model.RadioStackInfo
import com.flipperdevices.info.impl.model.RadioStackType
import com.flipperdevices.info.shared.ComposableDeviceInfoRow
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.ComposableLongDeviceInfoRowText
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.info.shared.getColorByChannel

@Composable
fun ComposableFullInfoDevice(
    fullDeviceInfo: DeviceFullInfo,
    inProgress: Boolean
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        InfoElementCard(Modifier, titleId = R.string.full_info_flipper_device) {
            ComposableFlipperDevicesInfo(fullDeviceInfo.flipperDevices, inProgress)
        }
        InfoElementCard(Modifier, titleId = R.string.full_info_firmware) {
            ComposableFirmwareInfo(fullDeviceInfo.firmware, inProgress)
        }
        InfoElementCard(Modifier, titleId = R.string.full_info_radio_stack) {
            ComposableRadioStackInfo(fullDeviceInfo.radioStack, inProgress)
        }
        InfoElementCard(Modifier, titleId = R.string.full_info_other) {
            ComposableOtherInfo(fullDeviceInfo.other.fields, inProgress)
        }
    }
}

@Composable
private fun ComposableFlipperDevicesInfo(info: FlipperDeviceInfo, inProgress: Boolean) {
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
    ComposableDeviceInfoRowWithText(
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
fun ComposableFirmwareInfo(info: FirmwareInfo, inProgress: Boolean) {
    ComposableSoftwareRevision(
        titleId = R.string.full_info_hardware_otp_version,
        inProgress = inProgress,
        value = info.softwareRevision,
        color = info.firmwareChannel?.let { getColorByChannel(it) } ?: Color.Transparent
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
        value = info.protobufVersion
    )
}

@Composable
fun ComposableRadioStackInfo(info: RadioStackInfo, inProgress: Boolean) {
    val text = if(isNotNull(info.type, info.radioFirmware)) {
        "${info.radioFirmware} (${getNameRadioStackType(info.type)})"
    } else null
    ComposableDeviceInfoRowWithText(
        titleId = R.string.full_info_radio_stack,
        inProgress = inProgress,
        value = text
    )
}

@Composable
private fun getNameRadioStackType(radioType: RadioStackType?): String {
    val textId = when (radioType) {
        RadioStackType.Full -> R.string.radio_stack_type_full
        RadioStackType.Light -> R.string.radio_stack_type_light
        RadioStackType.Beacon -> R.string.radio_stack_type_beacon
        RadioStackType.Basic -> R.string.radio_stack_type_basic
        RadioStackType.FullExtAdv -> R.string.radio_stack_type_full_ext_adv
        RadioStackType.HCIExtAdv -> R.string.radio_stack_type_hci_ext_adv
        RadioStackType.Unkwown, null -> R.string.radio_stack_type_unknown
    }
    return stringResource(id = textId)
}

@Composable
private fun ComposableOtherInfo(
    fields: Set<Map.Entry<String, String>>,
    inProgress: Boolean
) {
    fields.forEachIndexed { index, field ->
        val name = field.key.split(' ', '_')
            .map { it.titlecaseFirstCharIfItIsLowercase() }
            .joinToString(" ")
        val value = field.value

        ComposableDeviceInfoRowWithText(text = name, inProgress = inProgress, value = value)
        if (index != fields.size - 1) {
            ComposableInfoDivider()
        }
    }
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
