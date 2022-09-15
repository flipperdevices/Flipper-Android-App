package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.navigation.NavGraphRoute
import com.flipperdevices.info.impl.compose.screens.fullinfo.ComposableFullInfoDevice
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFullDeviceInfoScreen(
    navController: NavHostController,
    deviceInfoViewModel: DeviceInfoViewModel = tangleViewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = viewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    val deviceInfoRequestStatus by deviceInfoViewModel.getDeviceInfoRequestStatus().collectAsState()
    val localDeviceStatus = deviceStatus

    val inProgress = if (localDeviceStatus is DeviceStatus.NoDeviceInformation) {
        localDeviceStatus.connectInProgress
    } else {
        localDeviceStatus is DeviceStatus.Connected &&
            deviceInfoRequestStatus.rpcDeviceInfoRequestInProgress
    }

    val flipperRpcInformation by deviceInfoViewModel.getFlipperRpcInformation().collectAsState()

    Column {
        ComposableFullDeviceInfoScreenBar { navController.navigate(NavGraphRoute.Info.name) }
        ComposableFullInfoDevice(deviceInfoViewModel, flipperRpcInformation, inProgress)
    }
}

@Composable
private fun ComposableFullDeviceInfoScreenBar(onBack: () -> Unit) {
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
    }
}
