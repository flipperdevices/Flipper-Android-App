package com.flipperdevices.bridge.connection.config.api.serialization

import com.flipperdevices.core.preference.pb.SavedDevice
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object HardwareColorSerializer : KSerializer<SavedDevice.HardwareColor> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "com.flipperdevices.SavedDevice.HardwareColor",
        kind = PrimitiveKind.INT
    )

    override fun deserialize(decoder: Decoder): SavedDevice.HardwareColor {
        val intValue = decoder.decodeInt()
        return SavedDevice.HardwareColor.fromValue(intValue)
    }

    override fun serialize(encoder: Encoder, value: SavedDevice.HardwareColor) {
        encoder.encodeInt(value.value)
    }
}