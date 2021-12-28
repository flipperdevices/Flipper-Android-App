package com.flipperdevices.debug.impl.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableDebugScreen(
    goToStressTest: () -> Unit = {},
    startSynchronization: () -> Unit = {},
    sendAlarmToFlipper: () -> Unit = {}
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = buttonModifier,
            onClick = { startSynchronization() }
        ) {
            Text("Start synchronization")
        }
        Button(
            modifier = buttonModifier,
            onClick = { goToStressTest() }
        ) {
            Text("Stress Test")
        }
        Button(
            modifier = buttonModifier,
            onClick = { sendAlarmToFlipper() }
        ) {
            Text("Send alarm to flipper")
        }
    }
}
