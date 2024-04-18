package com.flipperdevices.faphub.screenshotspreview.api.serialization

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class ImmutableStringListSerializer(
    serializer: KSerializer<String>
) : KSerializer<ImmutableList<String>> {
    private val listSerializer = ListSerializer(serializer)

    private class StringListSerialDescriptor : SerialDescriptor by serialDescriptor<List<String>>() {
        override val serialName: String = "kotlinx.collections.immutable.ImmutableStringList"
    }

    override val descriptor: SerialDescriptor = StringListSerialDescriptor()

    override fun serialize(encoder: Encoder, value: ImmutableList<String>) {
        return listSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ImmutableList<String> {
        return listSerializer.deserialize(decoder).toImmutableList()
    }
}
