package com.flipperdevices.info.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.info.R
import no.nordicsemi.android.ble.ktx.state.ConnectionState

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposeInfoScreen(
    flipperGATTInformation: FlipperGATTInformation = FlipperGATTInformation(),
    connectionState: ConnectionState? = null,
    echoAnswers: List<ByteArray> = listOf("Test", "Test2").map { it.toByteArray() },
    echoListener: (String) -> Unit = {},
    connectionToAnotherDeviceButton: () -> Unit = {}
) {
    Column {
        Column(modifier = Modifier.weight(weight = 1f)) {
            InfoText(flipperGATTInformation, connectionState)
            EchoScreen(echoAnswers, echoListener)
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            onClick = connectionToAnotherDeviceButton
        ) {
            Text(text = "Connection to another device")
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

@Composable
private fun EchoScreen(
    echoAnswers: List<ByteArray>,
    echoListener: (String) -> Unit
) = Column {
    var text by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        TextField(
            modifier = Modifier.weight(weight = 1f),
            value = text,
            onValueChange = {
                text = it
            },
            label = { Text("Type text") }
        )
        Button(
            onClick = {
                text = ""
                echoListener.invoke(text)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_send_24),
                contentDescription = "Send"
            )
        }
    }
    if (echoAnswers.isEmpty()) {
        Text(
            text = "No echo answers yet",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.CenterHorizontally)
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            items(echoAnswers) { bytes ->
                EchoAnswer(String(bytes), bytes.toHex())
            }
        }
    }
}

@Composable
private fun EchoAnswer(echoAnswer: String, hexRepresentation: String) {
    Column(
        modifier = Modifier.padding(all = 16.dp)
    ) {
        Text(text = echoAnswer)
        Text(text = "HEX: $hexRepresentation")
    }
    Divider(color = Color.Black, thickness = 1.dp)
}

private fun ByteArray.toHex(): String =
    joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }

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
