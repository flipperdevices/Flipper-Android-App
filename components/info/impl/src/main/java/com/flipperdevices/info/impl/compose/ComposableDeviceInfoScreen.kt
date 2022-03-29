package com.flipperdevices.info.impl.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.flipperdevices.info.impl.compose.elements.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate

@Composable
fun ComposableDeviceInfoScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ComposableDeviceBar()
        ComposableFirmwareUpdate()
    }
}
