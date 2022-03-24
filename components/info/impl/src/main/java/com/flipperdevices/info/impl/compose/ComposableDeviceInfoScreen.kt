package com.flipperdevices.info.impl.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate

@Composable
fun ComposableDeviceInfoScreen() {
    Column {
        ComposableFirmwareUpdate()
    }
}
