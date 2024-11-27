package com.flipperdevices.bridge.connection.feature.storage.impl.fm.download

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.impl.utils.toRpc
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.FixedProgressListener
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.StatRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Source
import okio.buffer
import okio.use

class FFileDownloadApiImpl(
    private val rpcFeatureApi: FRpcFeatureApi,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) : FFileDownloadApi {
    override suspend fun download(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        priority: StorageRequestPriority,
        progressListener: FixedProgressListener?
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        info { "Start download file $pathOnFlipper to $fileOnAndroid" }

        runCatching {
            val sourceLength = getTotalSize(pathOnFlipper)
            fileSystem.sink(fileOnAndroid).buffer().use { sink ->
                source(pathOnFlipper, this, priority).use { source ->
                    source.copyWithProgress(
                        sink = sink,
                        progressListener = progressListener,
                        sourceLength = { sourceLength }
                    )
                }
            }
        }
    }

    override fun source(
        pathOnFlipper: String,
        scope: CoroutineScope,
        priority: StorageRequestPriority
    ): Source {
        return FFlipperSource(
            readerLoop = ReaderRequestLooper(
                rpcFeatureApi = rpcFeatureApi,
                scope = scope,
                priority = priority.toRpc(),
                pathOnFlipper = pathOnFlipper
            )
        )
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
