package com.flipperdevices.bridge.rpc.impl.delegates

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.model.NameWithHash
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.listRequest
import com.flipperdevices.protobuf.storage.md5sumRequest
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList

class FlipperListingDelegate @Inject constructor() {
    suspend fun listing(requestApi: FlipperRequestApi, pathOnFlipper: String): List<String> {
        return requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = pathOnFlipper
                }
            }.wrapToRequest()
        ).toList().mapNotNull { response ->
            if (response.commandStatus != Flipper.CommandStatus.OK) {
                error("Listing request failed for $pathOnFlipper with $response")
            } else if (response.hasStorageListResponse()) {
                response.storageListResponse.fileList
            } else {
                error("Can't find storage list response, $response")
            }
        }.flatten().map { it.name }
    }

    suspend fun listingWithMd5(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String
    ): List<NameWithHash> {
        return requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = pathOnFlipper
                    includeMd5 = true
                }
            }.wrapToRequest()
        ).toList().mapNotNull { response ->
            if (response.commandStatus != Flipper.CommandStatus.OK) {
                error("Listing request failed for $pathOnFlipper with $response")
            } else if (response.hasStorageListResponse()) {
                response.storageListResponse.fileList
            } else {
                error("Can't find storage list response, $response")
            }
        }.flatten().map { NameWithHash(it.name, it.md5Sum) }
    }

    suspend fun listingWithMd5Deprecated(
        requestApi: FlipperRequestApi,
        pathOnFlipper: String
    ): List<NameWithHash> {
        return listing(requestApi, pathOnFlipper).pmap { fileForMd5 ->
            val md5Response = requestApi.request(
                main {
                    storageMd5SumRequest = md5sumRequest {
                        path = fileForMd5
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
                name = fileForMd5,
                md5 = md5Response.storageMd5SumResponse.md5Sum
            )
        }
    }
}