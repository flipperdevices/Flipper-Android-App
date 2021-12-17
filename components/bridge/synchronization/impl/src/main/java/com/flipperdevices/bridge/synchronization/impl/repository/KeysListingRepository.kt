package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.synchronization.impl.model.KeyPath
import com.flipperdevices.bridge.synchronization.impl.model.ResultWithProgress
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage
import com.flipperdevices.protobuf.storage.listRequest
import java.io.File
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.toList

const val SIZE_BYTES_LIMIT = 10 * 1024 * 1024 // 10MiB

class KeysListingRepository : LogTagProvider {
    override val TAG = "KeysListingRepository"

    fun getAllKeys(
        requestApi: FlipperRequestApi
    ) = callbackFlow<ResultWithProgress<List<KeyPath>>> {
        val allKeys = mutableListOf<KeyPath>()
        FlipperFileType.values().forEach { fileType ->
            send(ResultWithProgress.InProgress())
            allKeys.addAll(getKeysForFileType(requestApi, fileType))
        }
        send(ResultWithProgress.Completed(allKeys))
        close()
    }

    private suspend fun getKeysForFileType(
        requestApi: FlipperRequestApi,
        fileType: FlipperFileType
    ): List<KeyPath> {
        val fileTypePath = File("/any/", fileType.flipperDir).absolutePath
        return requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = fileTypePath
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList().map { it.storageListResponse.fileList }.flatten()
            .filter { isValidFile(it) }
            .map {
                KeyPath(
                    path = File(fileTypePath, it.name).absolutePath,
                    name = it.name,
                    fileType = fileType,
                    byteSize = it.size
                )
            }
    }

    private fun isValidFile(file: Storage.File): Boolean {
        if (file.type != Storage.File.FileType.FILE) {
            debug {
                "File ${file.name} is not file. This is folder. Ignore it"
            }
            return false
        }
        if (file.size > SIZE_BYTES_LIMIT) {
            debug {
                "File ${file.name} skip, because current size limit is $SIZE_BYTES_LIMIT, " +
                    "but file size is ${file.size}"
            }
            return false
        }
        val extension = file.name.substringAfterLast(".")
        if (FlipperFileType.getByExtension(extension) == null) {
            debug {
                "File ${file.name} skip, because we don't support this file extension ($extension)"
            }
            return false
        }
        return true
    }
}
