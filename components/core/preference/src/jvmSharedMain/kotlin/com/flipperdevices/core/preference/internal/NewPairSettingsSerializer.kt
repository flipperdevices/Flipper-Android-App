package com.flipperdevices.core.preference.internal

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.flipperdevices.core.preference.pb.NewPairSettings
import java.io.InputStream
import java.io.OutputStream

internal object NewPairSettingsSerializer : Serializer<NewPairSettings> {
    override val defaultValue: NewPairSettings = NewPairSettings()

    override suspend fun readFrom(input: InputStream): NewPairSettings {
        try {
            return NewPairSettings.ADAPTER.decode(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: NewPairSettings, output: OutputStream) {
        t.encode(output)
    }
}
