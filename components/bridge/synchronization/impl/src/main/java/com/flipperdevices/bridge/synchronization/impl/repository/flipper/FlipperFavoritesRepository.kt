package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val FAVORITES_PATH = FlipperFilePath("/", "favorites.txt")

class FlipperFavoritesRepository : LogTagProvider {
    override val TAG = "FavoritesRepository"

    suspend fun getFavorites(flipperKeyStorage: FlipperKeyStorage): List<FlipperFilePath> {
        val favoritesPaths = getFavoritesFromFlipper(flipperKeyStorage)
        return favoritesPaths.map {
            val relativePath = it.replace(Constants.KEYS_DEFAULT_STORAGE, "").replace("/ext/", "")
            return@map relativePath.substringBefore("/") to relativePath.substringAfter("/")
        }.filter { (keyFolder, keyName) ->
            keyFolder.trim().isNotEmpty() || keyName.trim().isNotEmpty()
        }.map { (keyFolder, keyName) ->
            return@map FlipperFilePath(
                folder = keyFolder,
                nameWithExtension = keyName
            )
        }
    }

    private suspend fun getFavoritesFromFlipper(
        flipperKeyStorage: FlipperKeyStorage
    ): List<String> = withContext(Dispatchers.IO) {
        val favoritesFile = flipperKeyStorage.loadKey(
            FlipperKeyPath(FAVORITES_PATH, deleted = false)
        ).openStream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }
        return@withContext favoritesFile
            .split("\n")
    }

    suspend fun applyDiff(
        flipperKeyStorage: FlipperKeyStorage,
        oldFavorites: List<FlipperFilePath>,
        favoritesDiff: List<KeyDiff>
    ) {
        val resultFavoritesList = ArrayList(oldFavorites)
        for (diff in favoritesDiff) {
            when (diff.action) {
                KeyAction.ADD -> resultFavoritesList.add(diff.newHash.keyPath)
                KeyAction.MODIFIED -> resultFavoritesList.add(diff.newHash.keyPath)
                KeyAction.DELETED -> resultFavoritesList.remove(diff.newHash.keyPath)
            }
        }
        val newFavoritesFile = resultFavoritesList.joinToString("\n") {
            it.getPathOnFlipper()
        } + "\n"
        flipperKeyStorage.saveKey(
            FlipperKeyPath(FAVORITES_PATH, deleted = false),
            FlipperKeyContent.RawData(newFavoritesFile.toByteArray())
        )
    }
}
