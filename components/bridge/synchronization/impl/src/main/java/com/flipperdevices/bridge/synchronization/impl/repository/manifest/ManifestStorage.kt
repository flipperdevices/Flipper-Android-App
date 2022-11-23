package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import android.content.Context
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import com.flipperdevices.core.di.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

interface ManifestStorage {
    suspend fun save(manifest: ManifestFile)

    suspend fun load(): ManifestFile?
}

@SingleIn(TaskGraph::class)
@ContributesBinding(TaskGraph::class)
class ManifestStorageImpl @Inject constructor(context: Context) : ManifestStorage {
    private val file: File = File(context.filesDir, "LastSyncManifest_v4.json")

    override suspend fun save(
        manifest: ManifestFile
    ) = withContext(Dispatchers.IO) {
        if (file.exists()) {
            file.delete()
        }
        file.outputStream().use {
            Json.encodeToStream(manifest, it)
        }
    }

    override suspend fun load(): ManifestFile? = withContext(Dispatchers.IO) {
        if (!file.exists()) {
            return@withContext null
        }
        return@withContext file.inputStream().use {
            Json.decodeFromStream<ManifestFile>(it)
        }
    }
}
