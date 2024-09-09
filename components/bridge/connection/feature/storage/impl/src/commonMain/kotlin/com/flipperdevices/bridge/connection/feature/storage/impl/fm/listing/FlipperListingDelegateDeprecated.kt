package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcStorageNotExistException
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.fm.NameWithHash
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.ListRequest
import com.flipperdevices.protobuf.storage.Md5sumRequest
import kotlinx.coroutines.flow.toList
import okio.Path.Companion.toPath

class FlipperListingDelegateDeprecated(
    requestApi: FRpcFeatureApi
) : FlipperListingDelegate(requestApi) {
    override suspend fun listingWithMd5(
        pathOnFlipper: String
    ): List<NameWithHash> {
        return listingInternal(
            pathOnFlipper,
            includeMd5Flag = false
        ).pmap { fileForMd5 ->
            if (fileForMd5.type != File.FileType.FILE) {
                return@pmap null
            }
            if (fileForMd5.size > SIZE_BYTES_LIMIT) {
                return@pmap null
            }

            val md5Response = requestApi.requestOnce(
                Main(
                    storage_md5sum_request = Md5sumRequest(
                        path = pathOnFlipper.toPath().resolve(fileForMd5.name).toString()
                    )
                ).wrapToRequest()
            )

            val storageMd5Response = md5Response.getOrThrow().storage_md5sum_response
                ?: error("Can't find md5 response in $md5Response for $fileForMd5")


            NameWithHash(
                name = fileForMd5.name,
                md5 = storageMd5Response.md5sum,
                size = fileForMd5.size,
                type = fileForMd5.type.toInternalType()
            )
        }.filterNotNull()
    }
}