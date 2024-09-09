package com.flipperdevices.bridge.connection.feature.storage.api.fm

interface FListingStorageApi {
    suspend fun listingDirectory(pathOnFlipper: String): List<String>

    suspend fun listingDirectoryWithMd5(pathOnFlipper: String): List<NameWithHash>
}