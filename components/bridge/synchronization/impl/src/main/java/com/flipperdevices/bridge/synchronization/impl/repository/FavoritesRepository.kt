package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class FavoritesRepository : LogTagProvider {
    override val TAG = "FavoritesRepository"

    suspend fun getFavorites(requestApi: FlipperRequestApi): List<FlipperKey> {
        val favoritesPaths = getFavoritesFromFlipper(requestApi)
        val dirToKey = FlipperFileType.values().map { it.flipperDir to it }.toMap()
        return favoritesPaths.map {
            val relativePath = it.substringAfter("/")
            relativePath.substringBefore("/") to relativePath.substringAfter("/")
        }.map { (typeDir, keyName) ->
            dirToKey[typeDir] to keyName
        }.map { (keyType, keyName) ->
            if (keyType != null) {
                FlipperKey(keyName, keyType)
            } else null
        }.filterNotNull()
    }

    private suspend fun getFavoritesFromFlipper(
        requestApi: FlipperRequestApi
    ): List<String> = withContext(Dispatchers.IO) {
        return@withContext requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = "/any/favorites.txt"
                }
            }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
        ).toList()
            .map { it.storageReadResponse.file.data }
            .flatten()
            .toByteArray()
            .toString(Charset.defaultCharset())
            .split("\n")
    }
}
