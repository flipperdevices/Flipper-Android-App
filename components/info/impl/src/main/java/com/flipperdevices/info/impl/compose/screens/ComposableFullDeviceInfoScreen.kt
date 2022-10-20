package com.flipperdevices.info.impl.compose.screens

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.screens.fullinfo.ComposableFullInfoDevice
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FullInfoViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFullDeviceInfoScreen(
    navController: NavHostController,
    fullInfoViewModel: FullInfoViewModel = tangleViewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    val deviceInfoRequestStatus by fullInfoViewModel.getDeviceInfoRequestStatus().collectAsState()
    val localDeviceStatus = deviceStatus

    val inProgress = if (localDeviceStatus is DeviceStatus.NoDeviceInformation) {
        localDeviceStatus.connectInProgress
    } else {
        localDeviceStatus is DeviceStatus.Connected &&
                deviceInfoRequestStatus.rpcDeviceInfoRequestInProgress
    }

    val flipperRpcInformation by fullInfoViewModel.getFlipperRpcInformation().collectAsState()

    Column {
        ComposableFullDeviceInfoScreenBar(
            onBack = navController::popBackStack,
            onShare = fullInfoViewModel::shareDeviceInfo,
            inProgress = inProgress
        )
        ComposableFullInfoDevice(flipperRpcInformation, inProgress) {
            fullInfoViewModel.getFirmwareChannel(it)
        }
    }
}

@Composable
private fun ComposableFullDeviceInfoScreenBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    inProgress: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                .clickable(
                    indication = rememberRipple(bounded = false),
                    onClick = onBack,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .size(size = 24.dp),
            painter = painterResource(DesignSystem.drawable.ic_back),
            tint = LocalPallet.current.onAppBar,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 11.dp)
                .weight(weight = 1f),
            text = stringResource(R.string.info_device_info_title),
            style = LocalTypography.current.titleB20,
            color = LocalPallet.current.onAppBar
        )

        if (!inProgress) {
            Icon(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = onShare
                    )
                    .size(size = 24.dp),
                painter = painterResource(DesignSystem.drawable.ic_upload),
                contentDescription = null,
                tint = LocalPallet.current.onAppBar
            )
        } else {
            CircularProgressIndicator(
                color = LocalPallet.current.onAppBar,
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(size = 24.dp),
                strokeWidth = 2.5.dp
            )
        }
    }
}
