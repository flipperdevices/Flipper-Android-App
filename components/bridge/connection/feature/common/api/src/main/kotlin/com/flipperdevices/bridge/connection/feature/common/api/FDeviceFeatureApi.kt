package com.flipperdevices.bridge.connection.feature.common.api

import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import kotlinx.coroutines.CoroutineScope

interface FDeviceFeatureApi {
    fun interface Factory {
        operator fun invoke(
            unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
            scope: CoroutineScope,
            connectedDevice: FConnectedDeviceApi
        ): FDeviceFeatureApi?
    }
}
