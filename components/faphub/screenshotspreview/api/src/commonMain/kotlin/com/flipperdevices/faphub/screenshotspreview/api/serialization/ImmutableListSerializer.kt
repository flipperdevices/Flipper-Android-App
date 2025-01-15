package com.flipperdevices.faphub.screenshotspreview.api.serialization

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class ImmutableListSerializer<T>(
    serializer: KSerializer<T>
) : KSerializer<ImmutableList<T>> {
    private val listSerializer = ListSerializer(serializer)

    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: ImmutableList<T>) {
        return listSerializer.serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): ImmutableList<T> {
        return listSerializer.deserialize(decoder).toImmutableList()
    }
}
