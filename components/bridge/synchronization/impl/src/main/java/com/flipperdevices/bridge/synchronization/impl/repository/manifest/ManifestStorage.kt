package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import android.content.Context
import android.util.AtomicFile
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

interface ManifestStorage {
    suspend fun update(block: (ManifestFile) -> ManifestFile)

    suspend fun load(): ManifestFile?
}

@SingleIn(TaskGraph::class)
@ContributesBinding(TaskGraph::class, ManifestStorage::class)
class ManifestStorageImpl @Inject constructor(
    context: Context
) : ManifestStorage, LogTagProvider {
    override val TAG = "ManifestStorage"

    private val mutex = Mutex()

    private val file = AtomicFile(File(context.filesDir, "LastSyncManifest_v4.json"))

    override suspend fun update(block: (ManifestFile) -> ManifestFile) = withLock(mutex, "update") {
        val manifest = loadInternal() ?: ManifestFile(keys = emptyList())

        saveInternal(block(manifest))
    }

    override suspend fun load(): ManifestFile? = withLockResult(mutex, "load") {
        return@withLockResult loadInternal()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun saveInternal(manifestFile: ManifestFile) = withContext(
        FlipperDispatchers.workStealingDispatcher
    ) {
        file.delete()
        var os: FileOutputStream? = null
        try {
            os = file.startWrite()
            Json.encodeToStream(manifestFile, os)
            file.finishWrite(os)
        } catch (throwable: Throwable) {
            if (os != null) {
                file.failWrite(os)
            }
            throw throwable
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun loadInternal(): ManifestFile? = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (!file.baseFile.exists()) {
            return@withContext null
        }
        try {
            return@withContext file.openRead().use {
                Json.decodeFromStream<ManifestFile>(it)
            }
        } catch (throwable: Throwable) {
            error(throwable) { "Error while reading manifest file" }
            return@withContext null
        }
    }
}
