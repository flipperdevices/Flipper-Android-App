package com.flipperdevices.bridge.connection.connectionbuilder.api

import com.flipperdevices.bridge.connection.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.common.api.FTransportConnectionStatusListener
import kotlinx.coroutines.CoroutineScope

interface FDeviceConfigToConnection {
    suspend fun <API : FConnectedDeviceApi, CONFIG : FDeviceConnectionConfig<API>> connect(
        scope: CoroutineScope,
        config: CONFIG,
        listener: FTransportConnectionStatusListener
    ): Result<API>
}
