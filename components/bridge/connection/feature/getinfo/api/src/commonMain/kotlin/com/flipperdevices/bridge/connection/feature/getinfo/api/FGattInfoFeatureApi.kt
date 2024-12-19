package com.flipperdevices.bridge.connection.feature.getinfo.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGattInformation
import kotlinx.coroutines.flow.StateFlow

interface FGattInfoFeatureApi : FDeviceFeatureApi {
    fun getGattInfoFlow(): StateFlow<FGattInformation>
}
