package com.flipperdevices.bridge.connection.feature.storage.impl.fm.download

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.toThrowableFlow
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.impl.utils.toRpc
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.ReadRequest
import com.flipperdevices.protobuf.storage.StatRequest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

class FFileDownloadApiImpl(
    private val rpcFeatureApi: FRpcFeatureApi,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) : FFileDownloadApi {
    override suspend fun download(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        progressListener: ProgressListener?,
        priority: StorageRequestPriority
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        info { "Start download file $pathOnFlipper to ${fileOnAndroid}" }
        val progressListenerWrapper = progressListener?.let { ProgressWrapperTracker(it) }

        val totalSize = if (progressListenerWrapper != null) {
            getTotalSize(pathOnFlipper)
        } else {
            null
        }
        info { "Receive total size of file: $totalSize bytes" }


        fileSystem.sink(fileOnAndroid, mustCreate = true).buffer().use { output ->
            download(output, pathOnFlipper, priority) { downloaded ->
                if (progressListenerWrapper != null && totalSize != null) {
                    progressListenerWrapper.report(downloaded, totalSize)
                }
            }
        }
    }

    private suspend fun download(
        sink: BufferedSink,
        pathOnFlipper: String,
        storageRequestPriority: StorageRequestPriority,
        onProgress: suspend (downloaded: Long) -> Unit
    ) {
        var totalDownloaded = 0L

        rpcFeatureApi.request(
            Main(
                storage_read_request = ReadRequest(
                    path = pathOnFlipper
                )
            ).wrapToRequest(storageRequestPriority.toRpc())
        ).collect { response ->
            val data = response.getOrThrow().storage_read_response?.file_?.data_
                ?: error("Empty reponse")
            sink.write(data)
            totalDownloaded += data.size
            onProgress(totalDownloaded)
        }
        sink.flush()
    }

    private suspend fun getTotalSize(pathOnFlipper: String): Long {
        val listingList = rpcFeatureApi.request(
            Main(
                storage_stat_request = StatRequest(path = pathOnFlipper)
            ).wrapToRequest()
        ).toThrowableFlow().toList()
        if (listingList.isEmpty()) {
            error("Not received any response for listing $pathOnFlipper")
        }

        if (listingList.size > 1) {
            error(
                "Listing request return more than one response. " +
                        "Are you sure that you want download a file, not a directory?"
            )
        }

        return listingList.single().storage_stat_response?.file_?.size?.toLong()
            ?: error("Not found storage stat response")
    }
}