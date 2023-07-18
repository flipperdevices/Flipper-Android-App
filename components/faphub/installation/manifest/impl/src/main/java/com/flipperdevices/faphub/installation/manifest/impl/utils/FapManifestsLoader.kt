package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFEST_EXTENSION
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.listRequest
import com.flipperdevices.protobuf.storage.readRequest
import kotlinx.coroutines.flow.toList
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject

class FapManifestsLoader @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val parser: FapManifestParser
) : LogTagProvider {
    override val TAG = "FapManifestsLoader"

    suspend fun load(): List<FapManifestItem> {
        val serviceApi = flipperServiceProvider.getServiceApi()
        if (!serviceApi.connectionInformationApi.isDeviceConnected()) {
            throw FlipperNotConnected()
        }
        info { "Start load manifests" }
        var manifestNames = getManifestPaths(serviceApi.requestApi)
        info { "Find ${manifestNames.size} files" }
        manifestNames = manifestNames.filter { File(it).extension == FAP_MANIFEST_EXTENSION }
        info { "Find ${manifestNames.size} manifest files" }
        val fapItems = manifestNames.pmap { name ->
            val content = loadManifestFile(
                requestApi = serviceApi.requestApi,
                filePath = File(FAP_MANIFESTS_FOLDER_ON_FLIPPER, name).absolutePath
            ) ?: return@pmap null
            parser.parse(content, name)
        }.filterNotNull()
        info { "Parsed ${fapItems.size} manifests" }

        return fapItems
    }

    private suspend fun getManifestPaths(requestApi: FlipperRequestApi): List<String> {
        return requestApi.request(
            main {
                storageListRequest = listRequest {
                    path = FAP_MANIFESTS_FOLDER_ON_FLIPPER
                }
            }.wrapToRequest()
        ).toList().mapNotNull { response ->
            if (response.hasStorageListResponse()) {
                response.storageListResponse.fileList
            } else {
                info { "Can't find storage list response, $response" }
                null
            }
        }.flatten().map { it.name }
    }

    private suspend fun loadManifestFile(
        requestApi: FlipperRequestApi,
        filePath: String
    ): FlipperFileFormat? {
        val responseBytes = requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = filePath
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList().map { response ->
            if (response.hasStorageReadResponse()) {
                response.storageReadResponse.file.data.toByteArray()
            } else {
                return null
            }
        }.flatten()
        val textResponse = responseBytes.toString(Charset.forName("UTF-8"))

        return FlipperFileFormat.fromFileContent(textResponse)
    }
}
