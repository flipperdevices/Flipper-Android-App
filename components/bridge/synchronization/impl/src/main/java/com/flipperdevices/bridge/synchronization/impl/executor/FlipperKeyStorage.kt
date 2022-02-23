package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.protobuf.storage.writeRequest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList

class FlipperKeyStorage(
    private val requestApi: FlipperRequestApi
) : AbstractKeyStorage, LogTagProvider {
    override val TAG = "FlipperKeyStorage"

    override suspend fun loadKey(keyPath: FlipperKeyPath): FlipperKeyContent {
        val responseBytes = requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = keyPath.pathToKey
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList().map { it.storageReadResponse.file.data.toByteArray() }.flatten()

        return FlipperKeyContent.RawData(responseBytes)
    }

    override suspend fun saveKey(
        keyPath: FlipperKeyPath,
        keyContent: FlipperKeyContent
    ) = keyContent.openStream().use { stream ->
        val response = streamToCommandFlow(stream, keyContent.length()) { chunkData ->
            storageWriteRequest = writeRequest {
                path = keyPath.pathToKey
                file = file { data = chunkData }
            }
        }.map { it.wrapToRequest(FlipperRequestPriority.BACKGROUND) }.also {
            requestApi.request(it)
        }
        info { "File send with response $response" }
        return@use
    }

    override suspend fun deleteKey(keyPath: FlipperKeyPath) {
        requestApi.request(
            main {
                storageDeleteRequest = deleteRequest {
                    path = keyPath.pathToKey
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).single()
    }
}
