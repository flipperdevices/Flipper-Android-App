package com.flipperdevices.bridge.connection.device.fzero.impl.api

import com.flipperdevices.bridge.connection.device.fzero.api.FZeroDeviceApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

class FZeroDeviceApiImpl : FZeroDeviceApi {
    override fun getFeatureApi(feature: FDeviceFeature): FDeviceFeatureApi? {
        return null
    }
}
