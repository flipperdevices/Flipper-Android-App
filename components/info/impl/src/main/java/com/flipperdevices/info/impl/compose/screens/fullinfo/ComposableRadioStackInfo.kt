package com.flipperdevices.info.impl.compose.screens.fullinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.rpcinfo.model.RadioStackInfo
import com.flipperdevices.bridge.rpcinfo.model.RadioStackType
import com.flipperdevices.core.ktx.jre.isNotNull
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText

@Composable
fun ComposableRadioStackInfo(info: RadioStackInfo, inProgress: Boolean) {
    val text = if (isNotNull(info.type, info.radioFirmware)) {
        "${info.radioFirmware} (${getNameRadioStackType(info.type)})"
    } else {
        null
    }
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
