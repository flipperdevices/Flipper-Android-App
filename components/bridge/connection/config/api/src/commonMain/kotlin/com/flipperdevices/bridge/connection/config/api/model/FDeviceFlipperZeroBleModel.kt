package com.flipperdevices.bridge.connection.config.api.model

import com.flipperdevices.bridge.connection.config.api.FDeviceType
import com.flipperdevices.core.preference.pb.SavedDevice
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Serializable
data class FDeviceFlipperZeroBleModel(
    val name: String,
    val address: String,
    override val uniqueId: String = UUID.randomUUID().toString(),
    override val humanReadableName: String = "Flipper $name",
    private val intHardwareColor: Int
) : FDeviceBaseModel {
    override val type = FDeviceType.FLIPPER_ZERO_BLE

    @Transient
    val hardwareColor = SavedDevice.HardwareColor.fromValue(intHardwareColor)
}
