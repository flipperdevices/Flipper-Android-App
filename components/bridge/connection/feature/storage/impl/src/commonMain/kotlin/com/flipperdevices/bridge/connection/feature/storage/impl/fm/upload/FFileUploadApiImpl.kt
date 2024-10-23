package com.flipperdevices.bridge.connection.feature.storage.impl.fm.upload

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.impl.utils.toRpc
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.progress.FixedProgressListener
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.MkdirRequest
import com.flipperdevices.protobuf.storage.RenameRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
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
        priority: StorageRequestPriority,
        progressListener: FixedProgressListener?
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        runCatching {
            fileSystem.source(fileOnAndroid).buffer().use { source ->
                sink(pathOnFlipper, priority).use { sink ->
                    source.copyWithProgress(
                        sink,
                        progressListener,
                        sourceLength = {
                            fileSystem.metadata(fileOnAndroid).size
                        }
                    )
                }
            }
        }
    }

    override suspend fun mkdir(
        pathOnFlipper: String
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        rpcFeatureApi.requestOnce(
            Main(
                storage_mkdir_request = MkdirRequest(pathOnFlipper)
            ).wrapToRequest()
        ).map { }
    }

    override suspend fun sink(
        pathOnFlipper: String,
        priority: StorageRequestPriority
    ): Sink = FFlipperSink(
        requestLooper = WriteRequestLooper(rpcFeatureApi, pathOnFlipper, priority.toRpc(), scope),
    )

    override suspend fun move(
        oldPath: Path,
        newPath: Path,
    ): Result<Unit> = rpcFeatureApi.requestOnce(
        command = Main(
            storage_rename_request = RenameRequest(
                old_path = oldPath.toString(),
                new_path = newPath.toString()
            )
        ).wrapToRequest()
    ).map { }
}
