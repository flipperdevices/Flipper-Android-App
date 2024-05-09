package com.flipperdevices.bridge.connection.device.common.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

interface FDeviceApi {
    fun getFeatureApi(feature: FDeviceFeature): FDeviceFeatureApi?
}
