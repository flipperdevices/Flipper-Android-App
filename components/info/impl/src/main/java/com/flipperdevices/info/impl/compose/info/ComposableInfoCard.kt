package com.flipperdevices.info.impl.compose.info

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.elements.ComposableInfoCardContent
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.flipperdevices.info.shared.InfoElementCard
import com.flipperdevices.updater.model.FlipperUpdateState

@Composable
fun ComposableInfoCard(
    modifier: Modifier,
    onOpenFullDeviceInfo: () -> Unit,
    deviceStatusViewModel: DeviceStatusViewModel = viewModel(),
    firmwareUpdateViewModel: FirmwareUpdateViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    val updateStatus by deviceStatusViewModel.getUpdateState().collectAsState()
    val firmwareUpdateStatus by firmwareUpdateViewModel.getState().collectAsState()
    val isUnsupported = firmwareUpdateStatus != FlipperSupportedState.READY

    InfoElementCard(modifier, R.string.info_device_info_title) {
        if (updateStatus is FlipperUpdateState.Updating) {
            ComposableWaitingFlipper()
            return@InfoElementCard
        }
        ComposableInfoCardContent(isUnsupported)
        if (deviceStatus is DeviceStatus.Connected && !isUnsupported) {
            ComposableFullInfoButton(onOpenFullDeviceInfo)
        }
    }
}

@Composable
fun ComposableWaitingFlipper() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 68.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = LocalPallet.current.accentSecond,
            strokeWidth = 3.dp
        )
        Text(
            text = stringResource(id = R.string.info_firmware_waiting) + animatedDots(),
            color = LocalPallet.current.text30,
            style = LocalTypography.current.bodyR14,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ComposableFullInfoButton(
    onOpenFullDeviceInfo: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(),
                onClick = onOpenFullDeviceInfo,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(all = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.info_device_info_more_information),
            color = LocalPallet.current.text16,
            style = LocalTypography.current.bodyM14
        )
        Icon(
            modifier = Modifier
                .padding(start = 1.dp)
                .size(size = 12.dp),
            painter = painterResource(DesignSystem.drawable.ic_forward),
            contentDescription = stringResource(R.string.info_device_info_more_information),
            tint = LocalPallet.current.iconTint16
        )
    }
}
