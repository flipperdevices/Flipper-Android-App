package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.ui.ktx.SwipeRefresh
import com.flipperdevices.firstpair.impl.model.SearchingContent

@Composable
fun ComposableSearchingDevices(
    modifier: Modifier = Modifier,
    state: SearchingContent.FoundedDevices,
    onDeviceClick: (DiscoveredBluetoothDevice) -> Unit,
    onRefreshSearching: () -> Unit
) {
    val devices = state.devices

    SwipeRefresh(onRefreshSearching) {
        LazyColumn(
            modifier = modifier.padding(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = devices,
                key = { it.address }
            ) { device ->
                val deviceName = device.name ?: device.address
                val name = deviceName.replaceFirst(Constants.DEVICENAME_PREFIX, "")
                val isConnecting = device.address == state.selectedAddress
                ComposableSearchItem(text = name, isConnecting = isConnecting) {
                    onDeviceClick(device)
                }
            }
        }
    }
}
