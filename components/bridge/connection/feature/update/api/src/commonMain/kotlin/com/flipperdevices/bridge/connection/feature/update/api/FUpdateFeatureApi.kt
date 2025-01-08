package com.flipperdevices.bridge.connection.feature.update.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

interface FUpdateFeatureApi : FDeviceFeatureApi {
    fun displayApi(): DisplayApi
    fun bootApi(): BootApi
}
