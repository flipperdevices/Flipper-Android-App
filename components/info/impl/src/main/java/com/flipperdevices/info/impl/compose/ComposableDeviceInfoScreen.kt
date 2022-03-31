package com.flipperdevices.info.impl.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.info.impl.compose.elements.ComposableAlarmElement
import com.flipperdevices.info.impl.compose.elements.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.flipperdevices.info.impl.compose.elements.ComposableForgetElement
import com.flipperdevices.info.impl.compose.elements.ComposableSynchronizeElement

@Composable
fun ComposableDeviceInfoScreen() {
    Column {
        ComposableDeviceBar()
        ComposableFirmwareUpdate(modifier = Modifier.padding(top = 14.dp))
        ComposableSynchronizeElement(modifier = Modifier.padding(top = 24.dp))
        ComposableAlarmElement(modifier = Modifier.padding(top = 12.dp))
        ComposableForgetElement(modifier = Modifier.padding(top = 24.dp))
    }
}
