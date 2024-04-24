package com.flipperdevices.bridge.connection.config.api.model

import com.flipperdevices.bridge.connection.config.api.FDeviceType
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FDeviceFlipperZeroBleModel(
    val name: String,
    val address: String,
    override val uniqueId: String = UUID.randomUUID().toString(),
    override val humanReadableName: String = "Flipper $name"
) : FDeviceBaseModel {
    override val type = FDeviceType.FLIPPER_ZERO_BLE
}
