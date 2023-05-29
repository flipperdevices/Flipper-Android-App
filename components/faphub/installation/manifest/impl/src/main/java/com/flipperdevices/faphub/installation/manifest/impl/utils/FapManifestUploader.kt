package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.constants.FapHubConstants.FLIPPER_TMP_FOLDER
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFEST_EXTENSION
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.mkdirRequest
import com.flipperdevices.protobuf.storage.writeRequest
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val FLIPPER_TMP_MANIFEST_FILE = "$FLIPPER_TMP_FOLDER/tmp.fim"

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
        prepareFolder(serviceApi.requestApi)
        info { "Folder prepared" }
        uploadTmpManifest(serviceApi.requestApi, fapManifestItem)
        info { "Finish tmp manifest upload, path is $FLIPPER_TMP_MANIFEST_FILE" }
        return FLIPPER_TMP_MANIFEST_FILE
    }

    private suspend fun prepareFolder(requestApi: FlipperRequestApi) {
        val response = requestApi.request(
            main {
                storageMkdirRequest = mkdirRequest {
                    path = FLIPPER_TMP_FOLDER
                }
            }.wrapToRequest()
        ).first()
        if (response.commandStatus != Flipper.CommandStatus.OK) {
            error("Failed prepare tmp folder")
        }
    }

    private suspend fun uploadTmpManifest(
        requestApi: FlipperRequestApi,
        fapManifestItem: FapManifestItem
    ) {
        val fff = parser.encode(fapManifestItem)
        val response = fff.openStream().use { inputStream ->
            val requestFlow = streamToCommandFlow(inputStream, fff.length()) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = FLIPPER_TMP_MANIFEST_FILE
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
