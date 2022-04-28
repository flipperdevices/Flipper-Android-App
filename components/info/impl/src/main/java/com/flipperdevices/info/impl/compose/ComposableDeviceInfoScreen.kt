package com.flipperdevices.info.impl.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.info.impl.compose.bar.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableConnectedDeviceActionCard
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.flipperdevices.info.impl.compose.elements.ComposablePairDeviceActionCard
import com.flipperdevices.info.impl.compose.info.ComposableInfoCard
import com.flipperdevices.info.impl.compose.updater.ComposableUpdaterCard
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterUIApi

@Composable
fun ComposableDeviceInfoScreen(
    updaterApi: UpdaterApi,
    updaterUiApi: UpdaterUIApi
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ComposableDeviceBar()
        val isUpdaterAvailable by updaterUiApi.isUpdaterAvailable()
        if (isUpdaterAvailable) {
            ComposableUpdaterCard(
                modifier = Modifier.padding(top = 14.dp),
                updaterUiApi,
                updaterApi
            )
        }
        ComposableFirmwareUpdate(modifier = Modifier.padding(top = 14.dp))
        ComposableInfoCard(modifier = Modifier.padding(top = 14.dp))
        ComposableConnectedDeviceActionCard(modifier = Modifier.padding(top = 14.dp))
        ComposablePairDeviceActionCard(modifier = Modifier.padding(top = 14.dp, bottom = 14.dp))
    }
}
