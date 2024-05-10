package com.flipperdevices.bridge.connection.feature.rpc.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import kotlinx.coroutines.CoroutineScope

interface FRpcFeatureApi : FDeviceFeatureApi {
    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
            serialApi: FSerialDeviceApi
        ): FRpcFeatureApi
    }
}
