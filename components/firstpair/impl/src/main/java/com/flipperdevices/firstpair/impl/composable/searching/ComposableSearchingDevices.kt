package com.flipperdevices.firstpair.impl.composable.searching

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setDescription
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.model.DevicePairState
import com.flipperdevices.firstpair.impl.model.SearchingContent

@Composable
fun ComposableSearchingDevices(
    state: SearchingContent.FoundedDevices,
    onDeviceClick: (DiscoveredBluetoothDevice, resetPair: Boolean) -> Unit,
    onRefreshSearching: () -> Unit,
    onResetTimeoutState: () -> Unit,
    modifier: Modifier = Modifier
) {
    val devices = state.devices
    val currentDeviceConnecting = remember(state) {
        when (state.pairState) {
            is DevicePairState.Connecting -> state.pairState.address
            is DevicePairState.WaitingForStart -> state.pairState.address
            else -> null
        }
    }

    when (state.pairState) {
        is DevicePairState.TimeoutPairing -> {
            val pairDevice = state.pairState.discoveredBluetoothDevice
            ComposableConnectingTimeoutDialog(
                titleId = R.string.firstpair_retry_dialog_pairing_title,
                descId = R.string.firstpair_retry_dialog_pairing_desc,
                onRetry = { onDeviceClick(pairDevice, true) },
                onResetTimeoutState = onResetTimeoutState
            )
        }

        is DevicePairState.TimeoutConnecting -> {
            val pairDevice = state.pairState.discoveredBluetoothDevice
            ComposableConnectingTimeoutDialog(
                titleId = R.string.firstpair_retry_dialog_connecting_title,
                descId = R.string.firstpair_retry_dialog_connecting_desc,
                onRetry = { onDeviceClick(pairDevice, false) },
                onResetTimeoutState = onResetTimeoutState
            )
        }

        else -> {}
    }

    SwipeRefresh(
        modifier = modifier,
        onRefresh = onRefreshSearching
    ) {
        LazyColumn(
            modifier = Modifier.padding(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = devices,
                key = { it.address }
            ) { device ->
                val name = remember(device) {
                    val deviceName = device.name ?: device.address
                    deviceName.replaceFirst(Constants.DEVICENAME_PREFIX, "")
                }
                val isConnecting = remember(device.address, currentDeviceConnecting) {
                    device.address == currentDeviceConnecting
                }
                ComposableSearchItem(
                    text = name,
                    isConnecting = isConnecting,
                    onConnectionClick = { onDeviceClick(device, false) }
                )
            }
        }
    }
}

@Composable
private fun ComposableConnectingTimeoutDialog(
    @StringRes titleId: Int,
    @StringRes descId: Int,
    onRetry: () -> Unit,
    onResetTimeoutState: () -> Unit
) {
    val dialogModel = remember(onResetTimeoutState, onRetry) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(titleId)
            .setDescription(descId)
            .setOnDismissRequest(onResetTimeoutState)
            .addButton(R.string.firstpair_retry_dialog_retry_btn, onRetry, isActive = true)
            .addButton(R.string.firstpair_retry_dialog_cancel_btn, onResetTimeoutState)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}
