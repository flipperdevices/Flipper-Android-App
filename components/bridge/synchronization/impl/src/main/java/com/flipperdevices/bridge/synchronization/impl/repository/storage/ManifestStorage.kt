package com.flipperdevices.bridge.synchronization.impl.repository.storage

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

class ManifestStorage(context: Context) {
    private val file: File = File(context.filesDir, "LastSyncManifest_v3.json")

    suspend fun save(
        keys: List<KeyWithHash>,
        favorites: List<FlipperKeyPath>
    ) = withContext(Dispatchers.IO) {
        if (file.exists()) {
            file.delete()
        }
        file.outputStream().use {
            Json.encodeToStream(ManifestFile(keys, favorites), it)
        }
    }

    suspend fun load(): ManifestFile? = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            return@withContext null
        }
        return@withContext file.inputStream().use {
            Json.decodeFromStream<ManifestFile>(it)
        }
    }
}
