package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.ManifestFile
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.atomicfile.AtomicFile
import com.flipperdevices.core.atomicfile.SinkWithOutputStream
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
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.buffer
import javax.inject.Inject

interface ManifestStorage {
    suspend fun update(block: (ManifestFile) -> ManifestFile)

    suspend fun load(): ManifestFile?
}

@SingleIn(TaskGraph::class)
@ContributesBinding(TaskGraph::class, ManifestStorage::class)
class ManifestStorageImpl @Inject constructor(
    private val storageProvider: FlipperStorageProvider
) : ManifestStorage, LogTagProvider {
    override val TAG = "ManifestStorage"

    private val mutex = Mutex()

    private val file = AtomicFile(storageProvider.rootPath.resolve("LastSyncManifest_v4.json"))

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
        var os: SinkWithOutputStream? = null
        try {
            os = file.startWrite()
            Json.encodeToBufferedSink(manifestFile, os.buffer())
            file.finishWrite(os)
        } catch (throwable: Throwable) {
            if (os != null) {
                file.failWrite(os)
            }
            throw throwable
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun loadInternal(): ManifestFile? =
        withContext(FlipperDispatchers.workStealingDispatcher) {
            if (!storageProvider.fileSystem.exists(file.getBaseFile())) {
                return@withContext null
            }
            try {
                return@withContext file.openRead().use {
                    Json.decodeFromBufferedSource<ManifestFile>(it.buffer())
                }
            } catch (throwable: Throwable) {
                error(throwable) { "Error while reading manifest file" }
                return@withContext null
            }
        }
}
