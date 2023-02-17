package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.rpcinfo.model.dataOrNull
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.compose.screens.fullinfo.ComposableFullInfoDevice
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.BasicInfoViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.FullInfoViewModel
import com.flipperdevices.info.impl.viewmodel.deviceinfo.ShareFullInfoFileViewModel
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFullDeviceInfoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    shareViewModel: ShareFullInfoFileViewModel = viewModel(),
    basicInfoViewModel: BasicInfoViewModel = tangleViewModel(),
    fullInfoViewModel: FullInfoViewModel = tangleViewModel(),
    deviceStatusViewModel: DeviceStatusViewModel = tangleViewModel()
) {
    val deviceStatus by deviceStatusViewModel.getState().collectAsState()
    val localDeviceStatus = deviceStatus
    val flipperRpcInformation by fullInfoViewModel.getFlipperRpcInformation().collectAsState()
    val basicInfo by basicInfoViewModel.getDeviceInfo().collectAsState()

    val inProgress = when (localDeviceStatus) {
        is DeviceStatus.NoDeviceInformation -> {
            localDeviceStatus.connectInProgress
        }
        is DeviceStatus.Connected -> {
            flipperRpcInformation !is FlipperInformationStatus.Ready ||
                basicInfo.storageInfo.externalStorageStatus !is FlipperInformationStatus.Ready ||
                basicInfo.storageInfo.internalStorageStatus !is FlipperInformationStatus.Ready ||
                basicInfo.firmwareVersion !is FlipperInformationStatus.Ready
        }
        else -> false
    }

    Column(modifier = modifier) {
        ComposableFullDeviceInfoScreenBar(
            onBack = navController::popBackStack,
            onShare = {
                shareViewModel.shareDeviceInfo(flipperRpcInformation.dataOrNull(), basicInfo)
            },
            inProgress = inProgress
        )
        ComposableFullInfoDevice(
            fullDeviceInfo = flipperRpcInformation.dataOrNull() ?: FlipperRpcInformation(),
            inProgress = inProgress,
            getFirmwareChannel = fullInfoViewModel::getFirmwareChannel
        )
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
                .clickableRipple(bounded = false, onClick = onBack)
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
