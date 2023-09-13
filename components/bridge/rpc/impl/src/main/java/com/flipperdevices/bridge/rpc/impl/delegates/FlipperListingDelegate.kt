package com.flipperdevices.bridge.rpc.impl.delegates

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.model.NameWithHash
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.Storage
import com.flipperdevices.protobuf.storage.listRequest
import com.flipperdevices.protobuf.storage.md5sumRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import java.io.File
import javax.inject.Inject

private const val SIZE_BYTES_LIMIT = 10 * 1024 * 1024 // 10MiB

class FlipperListingDelegate @Inject constructor() {
    suspend fun listing(requestApi: FlipperRequestApi, pathOnFlipper: String): List<String> {
        return listingInternal(requestApi, pathOnFlipper, includeMd5Flag = false).map { it.name }
    }

    private suspend fun listingInternal(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String,
        includeMd5Flag: Boolean
    ): List<Storage.File> {
        return requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = pathOnFlipper
                    includeMd5 = includeMd5Flag
                    filterMaxSize = SIZE_BYTES_LIMIT
                }
            }.wrapToRequest()
        ).toList().map { response ->
            return@map when {
                response.commandStatus == Flipper.CommandStatus.ERROR_STORAGE_NOT_EXIST -> {
                    info { "Listing request for $pathOnFlipper with $response was not found" }
                    listOf()
                }

                response.commandStatus != Flipper.CommandStatus.OK -> {
                    error("Listing request failed for $pathOnFlipper with $response")
                }

                response.hasStorageListResponse() -> {
                    response.storageListResponse.fileList
                }

                else -> {
                    error("Can't find storage list response, $response")
                }
            }
        }.flatten()
    }

    suspend fun listingWithMd5(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String
    ): List<NameWithHash> {
        return listingInternal(
            requestApi,
            pathOnFlipper,
            includeMd5Flag = true
        ).mapNotNull {
            if (it.md5Sum.isNullOrBlank()) {
                return@mapNotNull null
            }
            NameWithHash(it.name, it.md5Sum, it.size, it.type)
        }
    }

    suspend fun listingWithMd5Deprecated(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String
    ): List<NameWithHash> {
        return listingInternal(
            requestApi,
            pathOnFlipper,
            includeMd5Flag = false
        ).pmap { fileForMd5 ->
            if (fileForMd5.type != Storage.File.FileType.FILE) {
                return@pmap null
            }
            if (fileForMd5.size > SIZE_BYTES_LIMIT) {
                return@pmap null
            }

            val md5Response = requestApi.request(
                main {
                    storageMd5SumRequest = md5sumRequest {
                        path = File(pathOnFlipper, fileForMd5.name).path
                    }
                }.wrapToRequest()
            ).first()
            if (md5Response.commandStatus != Flipper.CommandStatus.OK) {
                error("Md5 request failed for path $fileForMd5. Command status is ${md5Response.commandStatus}")
            }
            if (md5Response.hasStorageMd5SumResponse().not()) {
                error("Can't find md5 response in $md5Response for $fileForMd5")
            }
            NameWithHash(
                name = fileForMd5.name,
                md5 = md5Response.storageMd5SumResponse.md5Sum,
                size = fileForMd5.size,
                type = fileForMd5.type
            )
        }.filterNotNull()
    }
}
