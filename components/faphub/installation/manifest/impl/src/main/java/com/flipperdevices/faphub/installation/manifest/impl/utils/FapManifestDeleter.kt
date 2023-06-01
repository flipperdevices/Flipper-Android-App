package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import java.io.File
import javax.inject.Inject

class FapManifestDeleter @Inject constructor(
    private val flipperStorageApi: FlipperStorageApi
) {
    suspend fun delete(fapManifestItem: FapManifestItem) {
        val manifestPath = File(
            FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER,
            "${fapManifestItem.applicationAlias}.${FapManifestConstants.FAP_MANIFEST_EXTENSION}"
        ).path
        flipperStorageApi.delete(manifestPath)
        flipperStorageApi.delete(fapManifestItem.path)
    }
}