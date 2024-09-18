package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItemWithHash
import kotlinx.coroutines.flow.Flow

interface FListingStorageApi {
    suspend fun ls(
        pathOnFlipper: String
    ): Result<List<ListingItem>>

    suspend fun lsWithMd5(pathOnFlipper: String): Result<List<ListingItemWithHash>>

    suspend fun lsFlow(pathOnFlipper: String): Flow<Result<List<ListingItem>>>

    suspend fun lsWithMd5Flow(pathOnFlipper: String): Flow<Result<List<ListingItemWithHash>>>
}
