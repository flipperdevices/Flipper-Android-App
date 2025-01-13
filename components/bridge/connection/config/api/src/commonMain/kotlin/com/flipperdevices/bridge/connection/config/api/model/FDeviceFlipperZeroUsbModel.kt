package com.flipperdevices.bridge.connection.config.api.model

import com.flipperdevices.bridge.connection.config.api.FDeviceType
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class FDeviceFlipperZeroUsbModel(
    val name: String,
    val portPath: String,
    override val humanReadableName: String = "Flipper $name",
    override val uniqueId: String = UUID.randomUUID().toString(),
) : FDeviceBaseModel {
    override val type = FDeviceType.FLIPPER_ZERO_USB
}