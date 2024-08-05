package com.flipperdevices.bridge.connection.feature.getinfo.impl.mapper

import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiGroup
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty

class DeviceInfoKeyMapper {
    private val cache = FGetInfoApiProperty.DeviceInfo.entries.associateBy {
        it.key
    }

    fun map(path: String): FGetInfoApiProperty {
        val property = cache[path]
        if (property != null) {
            return property
        }
        return FGetInfoApiProperty.Unknown(FGetInfoApiGroup.DEVICE_INFO, path)
    }
}
