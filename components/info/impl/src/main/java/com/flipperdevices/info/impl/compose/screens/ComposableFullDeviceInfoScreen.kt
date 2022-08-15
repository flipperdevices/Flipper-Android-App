package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.flipperdevices.info.impl.compose.elements.ComposableFullInfoDevice
import com.flipperdevices.info.impl.compose.navigation.NavGraphRoute
import com.flipperdevices.info.impl.fragment.DeviceInfoHelper
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel

@Composable
fun ComposableFullDeviceInfoScreen(
    navController: NavHostController,
    deviceInfoViewModel: DeviceInfoViewModel = viewModel(),
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

    val verboseDeviceInfo by deviceInfoViewModel.getVerboseDeviceInfoState().collectAsState()
    val fields: Map<String, String> = verboseDeviceInfo.rpcInformationMap
    val fullDeviceInfo = DeviceInfoHelper.parseFields(fields)

    Column {
        ComposableFullDeviceInfoScreenBar { navController.navigate(NavGraphRoute.Info.name) }
        ComposableFullInfoDevice(fullDeviceInfo, inProgress)
    }
}

@Composable
private fun ComposableFullDeviceInfoScreenBar(exit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(),
                    onClick = exit,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 10.dp),
            painter = painterResource(DesignSystem.drawable.ic_back),
            tint = LocalPallet.current.onAppBar,
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.info_device_info_title),
            style = LocalTypography.current.titleB20,
            color = LocalPallet.current.onAppBar
        )
    }
}
