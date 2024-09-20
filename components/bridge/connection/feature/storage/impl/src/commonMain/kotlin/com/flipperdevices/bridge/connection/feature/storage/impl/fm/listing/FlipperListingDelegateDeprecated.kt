package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItemWithHash
import com.flipperdevices.core.ktx.jre.mapCatching
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.Md5sumRequest
import kotlinx.coroutines.flow.Flow
import okio.Path.Companion.toPath

class FlipperListingDelegateDeprecated(
    requestApi: FRpcFeatureApi
) : FlipperListingDelegate(requestApi) {
    override suspend fun listingWithMd5(
        pathOnFlipper: String
    ): Flow<Result<List<ListingItemWithHash>>> {
        return listingInternal(
            pathOnFlipper,
            includeMd5Flag = false
        ).mapCatching { list ->
            list.pmap { fileForMd5 ->
                calculateHash(pathOnFlipper, fileForMd5 = fileForMd5)
            }.filterNotNull()
        }
    }

    private suspend fun calculateHash(
        pathOnFlipper: String,
        fileForMd5: File
    ): ListingItemWithHash? {
        if (fileForMd5.type != File.FileType.FILE) {
            return null
        }
        if (fileForMd5.size > SIZE_BYTES_LIMIT) {
            return null
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

        return ListingItemWithHash(
            fileName = fileForMd5.name,
            md5 = storageMd5Response.md5sum,
            size = fileForMd5.size.toLong(),
            fileType = fileForMd5.type.toInternalType()
        )
    }
}
