package com.flipperdevices.info.impl.compose.screens.fullinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.shared.ComposableDeviceInfoRowWithText
import com.flipperdevices.info.shared.ComposableInfoDivider
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.model.FirmwareChannel
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun ComposableFullInfoDevice(
    fullDeviceInfo: FlipperRpcInformation,
    inProgress: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    getFirmwareChannel: (String?) -> FirmwareChannel?,
) = SwipeRefresh(modifier = modifier, onRefresh = onRefresh) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        InfoElementCard(
            Modifier,
            isSelectionArea = true,
            titleId = R.string.full_info_flipper_device
        ) {
            ComposableFlipperDevicesInfo(fullDeviceInfo.flipperDeviceInfo, inProgress)
        }
        InfoElementCard(
            Modifier,
            isSelectionArea = true,
            titleId = R.string.full_info_firmware
        ) {
            ComposableFirmwareInfo(fullDeviceInfo.firmware, inProgress) { getFirmwareChannel(it) }
        }
        InfoElementCard(
            Modifier,
            isSelectionArea = true,
            titleId = R.string.full_info_radio_stack
        ) {
            ComposableRadioStackInfo(fullDeviceInfo.radioStack, inProgress)
        }
        InfoElementCard(
            Modifier,
            isSelectionArea = true,
            titleId = R.string.full_info_other
        ) {
            ComposableOtherInfo(fullDeviceInfo.otherFields.entries, inProgress)
        }
    }
}

@Composable
private fun ComposableOtherInfo(
    fields: ImmutableSet<Map.Entry<String, String>>,
    inProgress: Boolean
) {
    fields.forEachIndexed { index, field ->
        val name = field.key.split(' ', '_', '.')
            .map { it.titlecaseFirstCharIfItIsLowercase() }
            .joinToString(" ")
        val value = field.value

        ComposableDeviceInfoRowWithText(text = name, inProgress = inProgress, value = value)
        if (index != fields.size - 1) {
            ComposableInfoDivider()
        }
    }
}
