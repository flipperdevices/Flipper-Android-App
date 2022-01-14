package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class FavoritesRepository : LogTagProvider {
    override val TAG = "FavoritesRepository"

    suspend fun getFavorites(requestApi: FlipperRequestApi): List<FlipperKeyPath> {
        val favoritesPaths = getFavoritesFromFlipper(requestApi)
        return favoritesPaths.map {
            val relativePath = it.replace("/any/", "").replace("/ext/", "")
            return@map relativePath.substringBefore("/") to relativePath.substringAfter("/")
        }.map { (keyFolder, keyName) ->
            return@map FlipperKeyPath(
                folder = keyFolder,
                name = keyName
            )
        }
    }

    private suspend fun getFavoritesFromFlipper(
        requestApi: FlipperRequestApi
    ): List<String> = withContext(Dispatchers.IO) {
        val responses = requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = "/any/favorites.txt"
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList()
        return@withContext responses
            .map { it.storageReadResponse.file.data }
            .flatten()
            .toByteArray()
            .toString(Charset.defaultCharset())
            .split("\n")
    }
}
