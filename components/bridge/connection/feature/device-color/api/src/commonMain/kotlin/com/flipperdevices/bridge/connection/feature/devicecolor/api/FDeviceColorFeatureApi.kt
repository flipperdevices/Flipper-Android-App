package com.flipperdevices.bridge.connection.feature.devicecolor.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.core.preference.pb.HardwareColor
import kotlinx.coroutines.flow.Flow

interface FDeviceColorFeatureApi : FDeviceFeatureApi {
    /**
     * Returns flipper case color
     * @param default default value on response failure
     */
    fun getColor(default: HardwareColor = HardwareColor.WHITE): Flow<HardwareColor>
}
