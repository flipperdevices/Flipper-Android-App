package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import java.io.File
import javax.inject.Inject

class FapManifestDeleter @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) : LogTagProvider {
    override val TAG: String = "FapManifestDeleter"

    suspend fun delete(fapManifestItem: FapManifestItem) {
        val manifestPath = File(
            FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER,
            "${fapManifestItem.applicationAlias}.${FapManifestConstants.FAP_MANIFEST_EXTENSION}"
        ).path
        val deleteApi = fFeatureProvider.getSync<FStorageFeatureApi>()?.deleteApi()
        if (deleteApi == null) {
            error { "#delete could not getSync deleteApi" }
            return
        }
        deleteApi.delete(manifestPath)
            .onFailure { error(it) { "#delete could not delete $manifestPath" } }
        deleteApi.delete(fapManifestItem.path)
            .onFailure { error(it) { "#delete could not delete ${fapManifestItem.path}" } }
    }
}
