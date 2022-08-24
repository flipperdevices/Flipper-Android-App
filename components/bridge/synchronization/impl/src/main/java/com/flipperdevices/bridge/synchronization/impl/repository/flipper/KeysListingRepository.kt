package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.model.ResultWithProgress
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.info
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
    ) = callbackFlow<ResultWithProgress<List<FlipperFilePath>>> {
        info { "Start request keys listing" }
        val allKeys = mutableListOf<FlipperFilePath>()
        FlipperKeyType.values().forEachIndexed { index, fileType ->
            send(ResultWithProgress.InProgress(index, FlipperKeyType.values().size))
            allKeys.addAll(getKeysForFileType(requestApi, fileType))
        }
        send(ResultWithProgress.Completed(allKeys))
        close()
    }

    private suspend fun getKeysForFileType(
        requestApi: FlipperRequestApi,
        fileType: FlipperKeyType
    ): List<FlipperFilePath> {
        val fileTypePath = File(Constants.KEYS_DEFAULT_STORAGE, fileType.flipperDir).path
        return requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = fileTypePath
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList().map { it.storageListResponse.fileList }.flatten()
            .filter { isValidFile(it, fileType) }
            .map {
                FlipperFilePath(
                    folder = fileType.flipperDir,
                    nameWithExtension = it.name
                )
            }
    }

    private fun isValidFile(file: Storage.File, requestedType: FlipperKeyType): Boolean {
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
        if (file.name.startsWith(".")) {
            debug {
                "File ${file.name} skip, because it starts with dot"
            }
            return false
        }
        val extension = file.name.substringAfterLast(".")

        if (FlipperFileType.getByExtension(extension) == FlipperFileType.SHADOW_NFC &&
            requestedType == FlipperKeyType.NFC
        ) {
            return true
        }
        val fileTypeByExtension = FlipperKeyType.getByExtension(extension)
        if (fileTypeByExtension == null) {
            debug {
                "File ${file.name} skip, because we don't support this file extension ($extension)"
            }
            return false
        }
        if (fileTypeByExtension != requestedType) {
            debug {
                "File ${file.name} skip, because folder type ($requestedType) " +
                    "and extension type ($fileTypeByExtension) is not equals"
            }
            return false
        }
        return true
    }
}
