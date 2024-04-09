package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class FDeviceHolder<API : FConnectedDeviceApi>(
    val config: FDeviceConnectionConfig<API>
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val connectJob: Job = scope.launch {

    }
}