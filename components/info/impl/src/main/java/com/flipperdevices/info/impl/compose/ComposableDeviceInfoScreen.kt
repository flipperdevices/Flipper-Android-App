package com.flipperdevices.info.impl.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.info.impl.compose.elements.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ComposableDeviceInfoScreen() {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = colorResource(DesignSystem.color.accent)

    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor)
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ComposableDeviceBar()
        ComposableFirmwareUpdate()
    }
}
