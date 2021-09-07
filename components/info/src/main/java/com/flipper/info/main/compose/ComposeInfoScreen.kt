package com.flipper.info.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipper.bridge.model.FlipperGATTInformation

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposeInfoScreen(
    flipperGATTInformation: FlipperGATTInformation = FlipperGATTInformation()
) {
    Column {
        Text(text = "Device name: ${flipperGATTInformation.deviceName ?: "Unavailable"}")
        Text(text = "Manufacturer: ${flipperGATTInformation.manufacturerName ?: "Unavailable"}")
        Text(text = "Hardware: ${flipperGATTInformation.hardwareRevision ?: "Unavailable"}")
        Text(text = "Firmware: ${flipperGATTInformation.softwareVersion ?: "Unavailable"}")
    }
}
