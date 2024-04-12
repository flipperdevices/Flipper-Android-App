package com.flipperdevices.bridge.connection.ble.api

import com.flipperdevices.bridge.connection.common.api.DeviceConnectionApi
import com.flipperdevices.bridge.connection.common.api.FTransportConnectionStatusListener
import kotlinx.coroutines.CoroutineScope

interface BleDeviceConnectionApi : DeviceConnectionApi<FBleApi, FBleDeviceConnectionConfig> {
    override suspend fun connect(
        scope: CoroutineScope,
        config: FBleDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): Result<FBleApi>
}
