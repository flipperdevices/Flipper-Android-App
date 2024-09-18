package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItemWithHash
import com.flipperdevices.core.ktx.jre.mapCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class FlipperListingDelegateNew(
    requestApi: FRpcFeatureApi
) : FlipperListingDelegate(requestApi) {
    override suspend fun listingWithMd5(
        pathOnFlipper: String
    ): Flow<Result<List<ListingItemWithHash>>> {
        return listingInternal(
            pathOnFlipper,
            includeMd5Flag = true
        ).mapCatching { list ->
            list.mapNotNull { file ->
                if (file.md5sum.isBlank()) {
                    return@mapNotNull null
                }
                ListingItemWithHash(
                    fileName = file.name,
                    md5 = file.md5sum,
                    size = file.size.toLong(),
                    fileType = file.type.toInternalType()
                )
            }
        }
    }
}
