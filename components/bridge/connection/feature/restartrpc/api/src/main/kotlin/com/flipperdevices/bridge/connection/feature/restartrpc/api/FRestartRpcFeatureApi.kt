package com.flipperdevices.bridge.connection.feature.restartrpc.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi

interface FRestartRpcFeatureApi : FDeviceFeatureApi {
    suspend fun restartRpc()

    fun interface Factory {
        operator fun invoke(
            transportRestartApi: FSerialRestartApi
        ): FRestartRpcFeatureApi
    }
}
