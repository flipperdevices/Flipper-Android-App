package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFEST_EXTENSION
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.utils.FapHubTmpFolderProvider
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class FapManifestUploader @Inject constructor(
    private val parser: FapManifestParser,
    private val flipperServiceProvider: FlipperServiceProvider,
    private val atomicMover: FapManifestAtomicMover
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
        ).absolutePath
        atomicMover.atomicMove(
            pathToFap to fapManifestItem.path,
            tmpManifestPath to newManifestPath
        )
    }

    private suspend fun saveToTmp(fapManifestItem: FapManifestItem): String {
        info { "Start save tmp manifest for ${fapManifestItem.applicationAlias}" }
        val serviceApi = flipperServiceProvider.getServiceApi()
        val tmpFapPath = File(
            FapHubTmpFolderProvider.provideTmpFolder(serviceApi.requestApi), "tmp.fim"
        ).absolutePath
        uploadTmpManifest(serviceApi.requestApi, fapManifestItem, tmpFapPath)
        info { "Finish tmp manifest upload, path is $tmpFapPath" }
        return tmpFapPath
    }

    private suspend fun uploadTmpManifest(
        requestApi: FlipperRequestApi,
        fapManifestItem: FapManifestItem,
        fapPath: String
    ) {
        val fff = parser.encode(fapManifestItem)
        val response = fff.openStream().use { inputStream ->
            val requestFlow = streamToCommandFlow(inputStream, fff.length()) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = fapPath
                    file = file { data = chunkData }
                }
            }.map { it.wrapToRequest() }

            requestApi.request(requestFlow)
        }

        if (response.commandStatus != Flipper.CommandStatus.OK) {
            error("Failed upload tmp manifest")
        }
    }
}
