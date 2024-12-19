package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFEST_EXTENSION
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.utils.FapHubConstants
import okio.buffer
import okio.source
import java.io.File
import javax.inject.Inject

class FapManifestUploader @Inject constructor(
    private val parser: FapManifestParser,
    private val fFeatureProvider: FFeatureProvider,
    private val atomicMover: FapManifestAtomicMover,
) : LogTagProvider {
    override val TAG = "FapManifestUploader"

    suspend fun save(
        pathToFap: String,
        fapManifestItem: FapManifestItem
    ) {
        val tmpManifestPath = saveToTmp(fapManifestItem)
        val newManifestPath = File(
            FAP_MANIFESTS_FOLDER_ON_FLIPPER,
            "${fapManifestItem.applicationAlias}.$FAP_MANIFEST_EXTENSION"
        ).path
        atomicMover.atomicMove(
            pathToFap to fapManifestItem.path,
            tmpManifestPath to newManifestPath
        )
    }

    private suspend fun saveToTmp(fapManifestItem: FapManifestItem): String {
        info { "Start save tmp manifest for ${fapManifestItem.applicationAlias}" }
        val uploadApi = fFeatureProvider.getSync<FStorageFeatureApi>()?.uploadApi()
        if (uploadApi == null) {
            error { "#uploadTmpManifest could not find uploadApi" }
            return FapHubConstants.FLIPPER_TMP_FOLDER_PATH
        }
        uploadApi.mkdir(FapHubConstants.FLIPPER_TMP_FOLDER_PATH)
            .onFailure { error(it) { "#saveToTmp could not mkdir ${FapHubConstants.FLIPPER_TMP_FOLDER_PATH}" } }
        val tmpFapPath = File(
            FapHubConstants.FLIPPER_TMP_FOLDER_PATH,
            "tmp.fim"
        ).path
        uploadTmpManifest(fapManifestItem, tmpFapPath)
        info { "Finish tmp manifest upload, path is $tmpFapPath" }
        return tmpFapPath
    }

    private suspend fun uploadTmpManifest(
        fapManifestItem: FapManifestItem,
        fapPath: String
    ) {
        val fff = parser.encode(fapManifestItem)

        val uploadApi = fFeatureProvider.getSync<FStorageFeatureApi>()?.uploadApi()
        if (uploadApi == null) {
            error { "#uploadTmpManifest could not find uploadApi" }
            return
        }
        fff.openStream().use { inputStream ->
            uploadApi.sink(pathOnFlipper = fapPath)
                .buffer()
                .use { sink -> inputStream.source().copyWithProgress(sink) }
        }
    }
}
