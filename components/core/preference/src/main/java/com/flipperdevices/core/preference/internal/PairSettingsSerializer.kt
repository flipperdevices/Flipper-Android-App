package com.flipperdevices.core.preference.internal

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.flipperdevices.core.preference.pb.PairSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

internal object PairSettingsSerializer : Serializer<PairSettings> {
    override val defaultValue: PairSettings = PairSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PairSettings {
        try {
            return PairSettings.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: PairSettings, output: OutputStream) {
        t.writeTo(output)
    }
}
