package com.flipperdevices.info.impl.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.info.impl.compose.elements.ComposableConnectedDeviceActionCard
import com.flipperdevices.info.impl.compose.elements.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.flipperdevices.info.impl.compose.elements.ComposablePairDeviceActionCard
import com.flipperdevices.info.impl.compose.elements.ComposableUpdaterCard
import com.flipperdevices.info.impl.compose.info.ComposableInfoCard
import com.flipperdevices.updater.api.UpdaterUIApi

@Composable
fun ComposableDeviceInfoScreen(updaterUiApi: UpdaterUIApi) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ComposableDeviceBar()
        ComposableUpdaterCard(modifier = Modifier.padding(top = 14.dp), updaterUiApi)
        ComposableFirmwareUpdate(modifier = Modifier.padding(top = 14.dp))
        ComposableInfoCard(modifier = Modifier.padding(top = 14.dp))
        ComposableConnectedDeviceActionCard(modifier = Modifier.padding(top = 14.dp))
        ComposablePairDeviceActionCard(modifier = Modifier.padding(top = 14.dp, bottom = 14.dp))
    }
}
