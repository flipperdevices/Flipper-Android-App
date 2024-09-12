package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDeleteApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import okio.buffer
import okio.source
import javax.inject.Inject

@StorageType(Platform.FLIPPER)
@ContributesBinding(TaskGraph::class, AbstractKeyStorage::class)
class FlipperKeyStorage @Inject constructor(
    private val uploadApi: FFileUploadApi,
    private val downloadApi: FFileDownloadApi,
    private val deleteApi: FFileDeleteApi
) : AbstractKeyStorage, LogTagProvider {
    override val TAG = "FlipperKeyStorage"

    override suspend fun loadFile(filePath: FlipperFilePath): FlipperKeyContent {
        val responseBytes = downloadApi.source(
            filePath.getPathOnFlipper(),
            priority = StorageRequestPriority.BACKGROUND
        ).buffer().readByteArray()

        return FlipperKeyContent.RawData(responseBytes)
    }

    override suspend fun modify(filePath: FlipperFilePath, newContent: FlipperKeyContent) {
        saveFile(filePath, newContent)
    }

    override suspend fun saveFile(
        filePath: FlipperFilePath,
        keyContent: FlipperKeyContent
    ) = keyContent.openStream().use { stream ->
        uploadApi.sink(
            filePath.getPathOnFlipper(),
            priority = StorageRequestPriority.BACKGROUND
        ).use { sink ->
            sink.buffer().writeAll(stream.source())
        }
        return@use
    }

    override suspend fun deleteFile(filePath: FlipperFilePath) {
        deleteApi.delete(
            filePath.getPathOnFlipper(),
            priority = StorageRequestPriority.BACKGROUND
        )
    }
}
