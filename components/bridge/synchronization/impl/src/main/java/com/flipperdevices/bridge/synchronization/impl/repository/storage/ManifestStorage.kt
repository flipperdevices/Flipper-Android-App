package com.flipperdevices.bridge.synchronization.impl.repository.storage

import android.content.Context
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

class ManifestStorage(context: Context) {
    private val file: File = File(context.filesDir, "LastSyncManifest")

    fun save(keys: List<KeyWithHash>) {
        if (file.exists()) {
            file.delete()
        }
        file.outputStream().use {
            Json.encodeToStream(ManifestFile(keys), it)
        }
    }

    suspend fun load(): ManifestFile? {
        if (!file.exists()) {
            return null
        }
        return file.inputStream().use {
            Json.decodeFromStream<ManifestFile>(it)
        }
    }
}
