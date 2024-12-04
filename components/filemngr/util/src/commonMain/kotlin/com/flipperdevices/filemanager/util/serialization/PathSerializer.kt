package com.flipperdevices.filemanager.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okio.Path
import okio.Path.Companion.toPath

object PathSerializer : KSerializer<Path> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "okio.Path",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Path {
        val path = decoder.decodeString()
        return path.toPath()
    }

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.toString())
    }
}
