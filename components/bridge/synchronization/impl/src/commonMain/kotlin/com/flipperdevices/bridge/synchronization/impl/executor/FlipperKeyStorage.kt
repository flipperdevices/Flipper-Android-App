package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.protobuf.storage.writeRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

@StorageType(Platform.FLIPPER)
@ContributesBinding(TaskGraph::class, AbstractKeyStorage::class)
class FlipperKeyStorage @Inject constructor(
    private val uploadApi: FFileUploadApi,
    private val downloadApi: FFileDownloadApi
) : AbstractKeyStorage, LogTagProvider {
    override val TAG = "FlipperKeyStorage"

    override suspend fun loadFile(filePath: FlipperFilePath): FlipperKeyContent {
        val responseBytes = requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = filePath.getPathOnFlipper()
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList().map { it.storageReadResponse.file.data.toByteArray() }.flatten()

        return FlipperKeyContent.RawData(responseBytes)
    }

    override suspend fun modify(filePath: FlipperFilePath, newContent: FlipperKeyContent) {
        saveFile(filePath, newContent)
    }

    override suspend fun saveFile(
        filePath: FlipperFilePath,
        keyContent: FlipperKeyContent
    ) = keyContent.openStream().use { stream ->
        val pathToFlipperFile = filePath.getPathOnFlipper()
        val response = streamToCommandFlow(stream, keyContent.length()) { chunkData ->
            storageWriteRequest = writeRequest {
                path = pathToFlipperFile
                file = file { data = chunkData }
            }
        }.map { it.wrapToRequest(FlipperRequestPriority.BACKGROUND) }.also {
            requestApi.request(it, onCancel = { id ->
                requestApi.request(
                    main {
                        commandId = id
                        hasNext = false
                        storageWriteRequest = writeRequest {
                            path = pathToFlipperFile
                        }
                    }.wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
                ).collect()
            })
        }
        info { "File send with response $response" }
        return@use
    }

    override suspend fun deleteFile(filePath: FlipperFilePath) {
        requestApi.request(
            main {
                storageDeleteRequest = deleteRequest {
                    path = filePath.getPathOnFlipper()
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).single()
    }
}
