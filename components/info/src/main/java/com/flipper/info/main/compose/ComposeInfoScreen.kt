package com.flipper.info.main.compose

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipper.bridge.model.FlipperGATTInformation
import com.flipper.info.R

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposeInfoScreen(
    flipperGATTInformation: FlipperGATTInformation = FlipperGATTInformation(),
    echoAnswers: List<ByteArray> = listOf("Test", "Test2").map { it.toByteArray() },
    echoListener: (String) -> Unit = {},
) {
    var text by rememberSaveable { mutableStateOf("") }

    Column {
        Text(text = "Device name: ${flipperGATTInformation.deviceName ?: "Unavailable"}")
        Text(text = "Manufacturer: ${flipperGATTInformation.manufacturerName ?: "Unavailable"}")
        Text(text = "Hardware: ${flipperGATTInformation.hardwareRevision ?: "Unavailable"}")
        Text(text = "Firmware: ${flipperGATTInformation.softwareVersion ?: "Unavailable"}")
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                label = { Text("Type text") }
            )
            Button(
                onClick = {
                    echoListener.invoke(text)
                }) {
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
