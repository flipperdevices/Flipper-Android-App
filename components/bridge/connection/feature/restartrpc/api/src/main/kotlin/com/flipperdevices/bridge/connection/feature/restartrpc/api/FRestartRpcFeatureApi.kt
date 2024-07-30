package com.flipperdevices.bridge.connection.feature.restartrpc.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

interface FRestartRpcFeatureApi : FDeviceFeatureApi {
    suspend fun restartRpc()
}
