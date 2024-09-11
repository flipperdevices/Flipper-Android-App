package com.flipperdevices.bridge.connection.feature.storage.impl.fm.upload

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.impl.utils.toRpc
import com.flipperdevices.bridge.connection.pbutils.ProtobufConstants
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.FileSystem
import okio.Path
import okio.Sink
import okio.buffer

class FFileUploadApiImpl(
    private val rpcFeatureApi: FRpcFeatureApi,
    private val scope: CoroutineScope,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) : FFileUploadApi, LogTagProvider {
    override val TAG = "FFileUploadApi"

    override suspend fun upload(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        progressListener: ProgressListener?,
        priority: StorageRequestPriority
    ): Unit = withContext(FlipperDispatchers.workStealingDispatcher) {
        val progressWrapper = progressListener?.let { ProgressWrapperTracker(it) }
        val length = fileSystem.metadata(fileOnAndroid).size

        fileSystem.source(fileOnAndroid).buffer().use { source ->
            sink(pathOnFlipper, priority).use { sink ->
                var totalBytesRead = 0L
                val buffer = Buffer()
                while (true) {
                    val readCount: Long =
                        source.read(buffer, ProtobufConstants.MAX_FILE_DATA.toLong())
                    if (readCount == -1L) break
                    sink.write(buffer, readCount)
                    totalBytesRead += readCount
                    if (length != null && progressWrapper != null) {
                        progressWrapper.report(totalBytesRead, length)
                    }
                }
            }
        }
    }

    override suspend fun sink(
        pathOnFlipper: String,
        priority: StorageRequestPriority
    ): Sink = FFlipperSink(
        requestLooper = WriteRequestLooper(rpcFeatureApi, pathOnFlipper, priority.toRpc(), scope),
    )
}

