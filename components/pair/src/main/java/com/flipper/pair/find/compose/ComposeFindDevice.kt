package com.flipper.pair.find.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipper.core.models.BLEDevice
import com.flipper.pair.R
import com.flipper.pair.find.service.BLEDeviceViewModel

private typealias OnDeviceClickListener = (BLEDevice) -> Unit

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposeFindDevice(
    bleDevices: BLEDeviceViewModel = BLEDeviceViewModel(),
    onDeviceClickListener: OnDeviceClickListener = {}
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.ic_scanning),
            contentDescription = stringResource(id = R.string.pair_finddevice_pic_search_devices),
        )
        Text(text = stringResource(id = R.string.pair_finddevice_title))

        val devices by bleDevices.state.collectAsState()
        DeviceList(onDeviceClickListener, devices)
    }
}

@Composable
fun DeviceList(
    onDeviceClickListener: OnDeviceClickListener,
    devices: List<BLEDevice>
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(devices) { device ->
            FlipperDevice(onDeviceClickListener, device)
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun FlipperDevice(
    onDeviceClickListener: OnDeviceClickListener = {},
    device: BLEDevice = BLEDevice("test", "Flipper Oebib")
) {
    TextButton(
        onClick = { onDeviceClickListener.invoke(device) }
    ) {
        Text(
            text = device.name,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}
