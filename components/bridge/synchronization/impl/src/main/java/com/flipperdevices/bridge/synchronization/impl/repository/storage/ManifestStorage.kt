package com.flipperdevices.bridge.synchronization.impl.repository.storage

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import javax.inject.Inject

interface ManifestStorage {
    suspend fun update(
        keys: List<KeyWithHash>? = null,
        favorites: List<FlipperFilePath>? = null,
        favoritesOnFlipper: List<FlipperFilePath>? = null
    )

    suspend fun load(): ManifestFile?
}

@SingleIn(TaskGraph::class)
@ContributesBinding(TaskGraph::class, ManifestStorage::class)
class ManifestStorageImpl @Inject constructor(
    context: Context
) : ManifestStorage, LogTagProvider {
    override val TAG = "ManifestStorage"

    private val mutex = Mutex()

    private val file: File = File(context.filesDir, "LastSyncManifest_v4.json")

    override suspend fun update(
        keys: List<KeyWithHash>?,
        favorites: List<FlipperFilePath>?,
        favoritesOnFlipper: List<FlipperFilePath>?
    ) = withLock(mutex, "update") {
        var manifest = loadInternal() ?: ManifestFile(keys = emptyList())
        if (keys != null) {
            manifest = manifest.copy(keys = keys)
        }
        if (favorites != null) {
            manifest = manifest.copy(favorites = favorites)
        }
        if (favoritesOnFlipper != null) {
            manifest = manifest.copy(favoritesFromFlipper = favoritesOnFlipper)
        }
        saveInternal(manifest)
    }

    override suspend fun load(): ManifestFile? = withLockResult(mutex, "load") {
        return@withLockResult loadInternal()
    }

    private suspend fun saveInternal(manifestFile: ManifestFile) = withContext(Dispatchers.IO) {
        if (file.exists()) {
            file.delete()
        }
        file.outputStream().use {
            Json.encodeToStream(manifestFile, it)
        }
    }

    private suspend fun loadInternal(): ManifestFile? = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            return@withContext null
        }
        return@withContext file.inputStream().use {
            Json.decodeFromStream<ManifestFile>(it)
        }
    }
}
