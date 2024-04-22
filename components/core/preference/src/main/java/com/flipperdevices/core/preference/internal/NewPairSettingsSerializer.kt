package com.flipperdevices.core.preference.internal

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.flipperdevices.core.preference.pb.NewPairSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

internal object NewPairSettingsSerializer : Serializer<NewPairSettings> {
    override val defaultValue: NewPairSettings = NewPairSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): NewPairSettings {
        try {
            return NewPairSettings.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: NewPairSettings, output: OutputStream) {
        t.writeTo(output)
    }
}
