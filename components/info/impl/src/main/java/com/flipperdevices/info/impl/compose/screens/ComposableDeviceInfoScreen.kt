package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.info.impl.compose.bar.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableConnectedDeviceActionCard
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.flipperdevices.info.impl.compose.elements.ComposableOptionsCard
import com.flipperdevices.info.impl.compose.elements.ComposablePairDeviceActionCard
import com.flipperdevices.info.impl.compose.info.ComposableInfoCard
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.info.impl.model.FlipperBasicInfo
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableDeviceInfoScreen(
    updaterCardApi: UpdaterCardApi,
    deeplink: Deeplink?,
    deviceStatus: DeviceStatus,
    scrollState: ScrollState,
    connectViewModel: ConnectViewModel,
    hardwareColor: HardwareColor,
    supportedState: FlipperSupportedState,
    updateState: FlipperUpdateState,
    deviceInfo: FlipperBasicInfo,
    onOpenFullDeviceInfo: () -> Unit,
    onOpenOptions: () -> Unit,
    alarmOnFlipper: () -> Unit,
    onStartUpdateRequest: (UpdateRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    var refreshRequested: Boolean by remember { mutableStateOf(false) }

    SwipeRefresh(
        modifier = modifier,
        onRefresh = { refreshRequested = true }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ComposableDeviceBar(deviceStatus, hardwareColor)
            updaterCardApi.ComposableUpdaterCard(
                modifier = Modifier.padding(top = 14.dp),
                onStartUpdateRequest = onStartUpdateRequest,
                requestRefresh = refreshRequested,
                onRefreshRequestExecuted = { refreshRequested = false },
                deeplink = deeplink
            )
            ComposableFirmwareUpdate(
                modifier = Modifier.padding(top = 14.dp),
                supportedState = supportedState
            )
            ComposableInfoCard(
                modifier = Modifier.padding(top = 14.dp),
                onOpenFullDeviceInfo = onOpenFullDeviceInfo,
                deviceStatus = deviceStatus,
                updateState = updateState,
                firmwareUpdateState = supportedState,
                deviceInfo = deviceInfo
            )
            ComposableOptionsCard(
                modifier = Modifier
                    .padding(top = 14.dp),
                onOpenOptions = onOpenOptions
            )
            ComposableConnectedDeviceActionCard(
                modifier = Modifier.padding(top = 14.dp),
                deviceStatus = deviceStatus,
                supportedState = supportedState,
                requestSynchronize = connectViewModel::requestSynchronize,
                alarmOnFlipper = alarmOnFlipper
            )
            ComposablePairDeviceActionCard(
                modifier = Modifier.padding(top = 14.dp, bottom = 14.dp),
                deviceStatus = deviceStatus,
                connectViewModel = connectViewModel
            )
        }
    }
}
