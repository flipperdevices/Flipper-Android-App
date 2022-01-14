package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.synchronization.impl.model.KeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import java.io.File
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class FavoritesRepository : LogTagProvider {
    override val TAG = "FavoritesRepository"

    suspend fun getFavorites(requestApi: FlipperRequestApi): List<KeyPath> {
        val favoritesPaths = getFavoritesFromFlipper(requestApi)
        val dirToKey = FlipperFileType.values().map { it.flipperDir to it }.toMap()
        return favoritesPaths.map {
            val relativePath = it.replace("/any/", "").replace("/ext/", "")
            relativePath.substringBefore("/") to relativePath.substringAfter("/")
        }.map { (typeDir, keyName) ->
            dirToKey[typeDir] to keyName
        }.mapNotNull { (keyType, keyName) ->
            if (keyType != null) {
                KeyPath(
                    path = File(keyType.flipperDir, keyName).path,
                    name = keyName,
                    fileType = keyType
                )
            } else null
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
