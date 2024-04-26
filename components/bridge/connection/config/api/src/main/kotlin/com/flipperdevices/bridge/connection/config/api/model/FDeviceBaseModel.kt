package com.flipperdevices.bridge.connection.config.api.model

import com.flipperdevices.bridge.connection.config.api.FDeviceType

sealed interface FDeviceBaseModel {
    val humanReadableName: String
    val type: FDeviceType
    val uniqueId: String
}
