package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.ktx.jre.pmap
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.errors.api.throwable.FlipperNotConnected
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestConstants.FAP_MANIFESTS_FOLDER_ON_FLIPPER
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import kotlinx.coroutines.flow.toList
import java.io.File
import javax.inject.Inject

class FapManifestsLoader @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val parser: FapManifestParser,
    private val cacheLoader: FapManifestCacheLoader
) : LogTagProvider {

    override val TAG = "FapManifestsLoader"

    suspend fun load(): List<FapManifestItem> {
        val serviceApi = flipperServiceProvider.getServiceApi()
        if (!serviceApi.connectionInformationApi.isDeviceConnected()) {
            throw FlipperNotConnected()
        }
        info { "Start load manifests" }
        val cacheResult = cacheLoader.loadCache()
        info { "Cache load result is toLoad: ${cacheResult.toLoadNames}, cached: ${cacheResult.cachedNames}" }
        val fapItemsFromFlipper = cacheResult.toLoadNames.pmap { name ->
            val content = loadManifestFile(
                requestApi = serviceApi.requestApi,
                filePath = File(FAP_MANIFESTS_FOLDER_ON_FLIPPER, name).absolutePath
            ) ?: return@pmap null
            parser.parse(content, name)
        }.filterNotNull()
        info { "Parsed ${fapItemsFromFlipper.size} manifests from flipper" }
        val fapItemsFromCache = cacheResult.cachedNames.pmap { (file, name) ->
            parser.parse(file.readBytes(), name)
        }.filterNotNull()
        info { "Parsed ${fapItemsFromCache.size} manifests from cache" }

        return fapItemsFromFlipper + fapItemsFromCache
    }

    private suspend fun loadManifestFile(
        requestApi: FlipperRequestApi,
        filePath: String
    ): ByteArray? {
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

        return responseBytes
    }
}
