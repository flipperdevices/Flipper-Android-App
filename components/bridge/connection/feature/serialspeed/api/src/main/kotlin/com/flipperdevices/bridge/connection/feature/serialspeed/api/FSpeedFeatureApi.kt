package com.flipperdevices.bridge.connection.feature.serialspeed.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import kotlinx.coroutines.flow.StateFlow

interface FSpeedFeatureApi : FDeviceFeatureApi {
    suspend fun getSpeed(): StateFlow<FlipperSerialSpeed>

    fun interface Factory : FDeviceFeatureApi.Factory
}
