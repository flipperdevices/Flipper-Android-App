package com.flipperdevices.bridge.connection.config.api.model

import com.flipperdevices.bridge.connection.config.api.FDeviceType
import com.flipperdevices.bridge.connection.config.api.serialization.HardwareColorSerializer
import com.flipperdevices.core.preference.pb.FlipperZeroBle
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FDeviceFlipperZeroBleModel(
    val name: String,
    val address: String,
    override val uniqueId: String = UUID.randomUUID().toString(),
    override val humanReadableName: String = "Flipper $name",
    @Serializable(HardwareColorSerializer::class)
    val hardwareColor: FlipperZeroBle.HardwareColor
) : FDeviceBaseModel {
    override val type = FDeviceType.FLIPPER_ZERO_BLE
}
