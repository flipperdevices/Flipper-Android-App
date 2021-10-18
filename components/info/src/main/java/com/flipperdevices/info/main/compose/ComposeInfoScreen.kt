package com.flipperdevices.info.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import no.nordicsemi.android.ble.ktx.state.ConnectionState

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposeInfoScreen(
    flipperGATTInformation: FlipperGATTInformation = FlipperGATTInformation(),
    connectionState: ConnectionState? = null
) {
    Column {
        Column(modifier = Modifier.weight(weight = 1f)) {
            InfoText(flipperGATTInformation, connectionState)
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

private fun ConnectionState.toHumanReadableString(): String {
    return when (this) {
        ConnectionState.Connecting -> "Connecting"
        ConnectionState.Initializing -> "Initializing"
        ConnectionState.Ready -> "Ready"
        ConnectionState.Disconnecting -> "Disconnecting"
        is ConnectionState.Disconnected -> "Disconnected"
        else -> this::class.simpleName ?: this.toString()
    }
}
