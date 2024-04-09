package com.flipperdevices.bridge.connection.common.api

import kotlinx.coroutines.CoroutineScope

interface DeviceConnectionApi<API : FConnectedDeviceApi, CONFIG : FDeviceConnectionConfig<API>> {
    suspend fun connect(
        scope: CoroutineScope,
        config: CONFIG,
        listener: FTransportConnectionStatusListener
    ): Result<API>
}
