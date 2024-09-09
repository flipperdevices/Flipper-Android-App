package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.NameWithHash

class FListingStorageApiImpl(
    private val listingDelegate: FlipperListingDelegate,
) : FListingStorageApi {
    override suspend fun listingDirectory(pathOnFlipper: String): List<String> {
        return listingDelegate.listing(
            pathOnFlipper = pathOnFlipper
        )
    }

    override suspend fun listingDirectoryWithMd5(pathOnFlipper: String): List<NameWithHash> {
        return listingDelegate.listingWithMd5(
            pathOnFlipper = pathOnFlipper
        )
    }
}