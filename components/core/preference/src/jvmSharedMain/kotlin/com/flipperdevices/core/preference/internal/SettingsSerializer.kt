package com.flipperdevices.core.preference.internal

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.flipperdevices.core.preference.pb.Settings
import java.io.InputStream
import java.io.OutputStream

internal object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.ADAPTER.decode(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        t.encode(output)
    }
}
