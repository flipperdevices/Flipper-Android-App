package com.flipperdevices.info.impl.main.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.manager.delegates.toHumanReadableString
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.main.model.DeviceSubScreen
import no.nordicsemi.android.ble.ktx.state.ConnectionState

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposeInfoScreen(
    flipperGATTInformation: FlipperGATTInformation = FlipperGATTInformation(),
    connectionState: ConnectionState? = null,
    onOpenScreen: (DeviceSubScreen) -> Unit = {},
    connectionToAnotherDeviceButton: () -> Unit = {}
) {
    Column {
        Column(modifier = Modifier.weight(weight = 1f)) {
            InfoText(flipperGATTInformation, connectionState)
        }

        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onOpenScreen(DeviceSubScreen.DEBUG) }
            ) {
                Text(text = stringResource(R.string.info_debug))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onOpenScreen(DeviceSubScreen.FILE_MANAGER) }
            ) {
                Text(text = stringResource(R.string.info_file_manager))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onOpenScreen(DeviceSubScreen.SCREEN_STREAMING) }
            ) {
                Text(text = stringResource(R.string.info_screen_streaming))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = connectionToAnotherDeviceButton
            ) {
                Text(text = stringResource(R.string.info_connection_to_another_device))
            }
        }
    }
}

@Composable
private fun InfoText(
    flipperGATTInformation: FlipperGATTInformation,
    connectionState: ConnectionState?
) {
    Text(text = "Connection status: ${connectionState?.toHumanReadableString() ?: "Unconnected"}")
    Text(text = "Device name: ${flipperGATTInformation.deviceName ?: "Unavailable"}")
    Text(text = "Manufacturer: ${flipperGATTInformation.manufacturerName ?: "Unavailable"}")
    Text(text = "Hardware: ${flipperGATTInformation.hardwareRevision ?: "Unavailable"}")
    Text(text = "Firmware: ${flipperGATTInformation.softwareVersion ?: "Unavailable"}")
}
