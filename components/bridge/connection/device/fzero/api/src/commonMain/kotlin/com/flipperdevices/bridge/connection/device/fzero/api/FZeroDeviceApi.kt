package com.flipperdevices.bridge.connection.device.fzero.api

import com.flipperdevices.bridge.connection.device.common.api.FDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import kotlinx.coroutines.CoroutineScope

interface FZeroDeviceApi : FDeviceApi {

    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
            connectedDevice: FConnectedDeviceApi
        ): FZeroDeviceApi
    }
}
