package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.NameWithHash

class FlipperListingDelegateNew(
    requestApi: FRpcFeatureApi
) : FlipperListingDelegate(requestApi) {
    override suspend fun listingWithMd5(
        pathOnFlipper: String
    ): List<NameWithHash> {
        return listingInternal(
            pathOnFlipper,
            includeMd5Flag = true
        ).mapNotNull {
            if (it.md5sum.isBlank()) {
                return@mapNotNull null
            }
            NameWithHash(
                it.name,
                it.md5sum,
                it.size,
                it.type.toInternalType()
            )
        }
    }
}
