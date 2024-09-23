package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.core.ktx.jre.flattenCatching
import kotlinx.coroutines.flow.toList

class FListingStorageApiImpl(
    private val listingDelegate: FlipperListingDelegate,
) : FListingStorageApi {
    override suspend fun ls(
        pathOnFlipper: String
    ) = listingDelegate.listing(
        pathOnFlipper = pathOnFlipper
    ).toList().flattenCatching()

    override suspend fun lsWithMd5(
        pathOnFlipper: String
    ) = listingDelegate.listingWithMd5(
        pathOnFlipper = pathOnFlipper
    ).toList().flattenCatching()

    override suspend fun lsFlow(
        pathOnFlipper: String
    ) = listingDelegate.listing(
        pathOnFlipper = pathOnFlipper
    )

    override suspend fun lsWithMd5Flow(
        pathOnFlipper: String
    ) = listingDelegate.listingWithMd5(
        pathOnFlipper = pathOnFlipper
    )
}
