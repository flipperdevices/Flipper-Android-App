package com.flipperdevices.bridge.connection.feature.common.api


/**
 * A special kind of FDeviceFeatureApi that is called immediately when the device is connected and is ready to be used
 */
interface FOnDeviceReadyFeatureApi : FDeviceFeatureApi {
    suspend fun onReady()

    fun interface Factory : FDeviceFeatureApi.Factory
}