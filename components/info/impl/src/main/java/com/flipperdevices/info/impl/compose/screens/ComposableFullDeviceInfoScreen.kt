package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.screens.fullinfo.ComposableFullInfoDevice
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FullInfoViewModel
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFullDeviceInfoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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

    Column(modifier = modifier) {
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
    OrangeAppBar(
        titleId = R.string.info_device_info_title,
        onBack = onBack,
    ) {
        if (!inProgress) {
            Icon(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .clickableRipple(bounded = false, onClick = onShare)
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
