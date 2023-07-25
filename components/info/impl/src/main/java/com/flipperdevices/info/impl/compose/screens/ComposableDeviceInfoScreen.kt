package com.flipperdevices.info.impl.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.info.impl.compose.bar.ComposableDeviceBar
import com.flipperdevices.info.impl.compose.elements.ComposableConnectedDeviceActionCard
import com.flipperdevices.info.impl.compose.elements.ComposableFirmwareUpdate
import com.flipperdevices.info.impl.compose.elements.ComposableOptionsCard
import com.flipperdevices.info.impl.compose.elements.ComposablePairDeviceActionCard
import com.flipperdevices.info.impl.compose.info.ComposableInfoCard
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.model.UpdateRequest

@Composable
fun ComposableDeviceInfoScreen(
    updaterCardApi: UpdaterCardApi,
    onOpenFullDeviceInfo: () -> Unit,
    onOpenOptions: () -> Unit,
    onStartUpdateRequest: (UpdateRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    var refreshRequested: Boolean by remember { mutableStateOf(false) }

    SwipeRefresh(onRefresh = { refreshRequested = true }) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ComposableDeviceBar()
            updaterCardApi.ComposableUpdaterCard(
                modifier = Modifier.padding(top = 14.dp),
                onStartUpdateRequest = onStartUpdateRequest,
                requestRefresh = refreshRequested,
                onRefreshRequestExecuted = { refreshRequested = false }
            )
            ComposableFirmwareUpdate(modifier = Modifier.padding(top = 14.dp))
            ComposableInfoCard(
                modifier = Modifier.padding(top = 14.dp),
                onOpenFullDeviceInfo = onOpenFullDeviceInfo
            )
            ComposableOptionsCard(
                modifier = Modifier
                    .padding(top = 14.dp),
                onOpenOptions = onOpenOptions
            )
            ComposableConnectedDeviceActionCard(modifier = Modifier.padding(top = 14.dp))
            ComposablePairDeviceActionCard(modifier = Modifier.padding(top = 14.dp, bottom = 14.dp))
        }
    }
}
