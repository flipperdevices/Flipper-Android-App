package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import javax.inject.Inject

private val FAVORITES_PATH = FlipperFilePath("/", "favorites.txt")

interface FlipperFavoritesRepository {
    suspend fun getFavorites(flipperKeyStorage: FlipperKeyStorage): List<FlipperFilePath>
    suspend fun applyDiff(
        flipperKeyStorage: FlipperKeyStorage,
        oldFavorites: List<FlipperFilePath>,
        favoritesDiff: List<KeyDiff>
    ): List<FlipperFilePath>
}

@ContributesBinding(TaskGraph::class, FlipperFavoritesRepository::class)
class FlipperFavoritesRepositoryImpl @Inject constructor() :
    FlipperFavoritesRepository,
    LogTagProvider {
    override val TAG = "FavoritesRepository"

    override suspend fun getFavorites(flipperKeyStorage: FlipperKeyStorage): List<FlipperFilePath> {
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
    ): List<String> = withContext(FlipperDispatchers.workStealingDispatcher) {
        val favoritesFile = flipperKeyStorage.loadFile(
            FAVORITES_PATH
        ).openStream().use {
            it.readBytes().toString(Charset.defaultCharset())
        }
        return@withContext favoritesFile
            .split("\n")
    }

    override suspend fun applyDiff(
        flipperKeyStorage: FlipperKeyStorage,
        oldFavorites: List<FlipperFilePath>,
        favoritesDiff: List<KeyDiff>
    ): List<FlipperFilePath> {
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
        flipperKeyStorage.saveFile(
            FAVORITES_PATH,
            FlipperKeyContent.RawData(newFavoritesFile.toByteArray())
        )
        return resultFavoritesList
    }
}
