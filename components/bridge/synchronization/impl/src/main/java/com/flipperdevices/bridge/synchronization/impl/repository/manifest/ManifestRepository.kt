package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface ManifestRepository {
    suspend fun updateManifest(
        keys: List<KeyWithHash>,
    )

    suspend fun updateManifest(
        favorites: List<FlipperFilePath>,
        favoritesOnFlipper: List<FlipperFilePath>
    )

    suspend fun compareFolderKeysWithManifest(
        folder: String,
        keys: List<KeyWithHash>,
        diffSource: DiffSource
    ): List<KeyDiff>

    suspend fun compareFavoritesWithManifest(
        favorites: List<FlipperFilePath>
    ): List<KeyDiff>

    suspend fun compareFlipperFavoritesWithManifest(
        favoritesOnFlipper: List<FlipperFilePath>
    ): List<KeyDiff>

    suspend fun getFavorites(): List<FlipperFilePath>?
}

@ContributesBinding(TaskGraph::class, ManifestRepository::class)
class ManifestRepositoryImpl @Inject constructor(
    private val manifestStorage: ManifestStorage
) : ManifestRepository {
    override suspend fun updateManifest(
        keys: List<KeyWithHash>
    ) {
        manifestStorage.update { it.copy(keys = keys) }
    }

    override suspend fun updateManifest(
        favorites: List<FlipperFilePath>,
        favoritesOnFlipper: List<FlipperFilePath>
    ) {
        manifestStorage.update {
            it.copy(
                favorites = favorites,
                favoritesFromFlipper = favoritesOnFlipper
            )
        }
    }

    override suspend fun compareFolderKeysWithManifest(
        folder: String,
        keys: List<KeyWithHash>,
        diffSource: DiffSource
    ): List<KeyDiff> {
        val manifestFile = manifestStorage.load()
            ?: return keys.map { KeyDiff(it, KeyAction.ADD, diffSource) }
        val keysFromManifest = manifestFile.keys.filter {
            it.keyPath.folder == folder
        }
        return compare(keysFromManifest, keys, diffSource)
    }

    override suspend fun compareFavoritesWithManifest(
        favorites: List<FlipperFilePath>
    ): List<KeyDiff> {
        val manifestFile = manifestStorage.load()
            ?: return favorites.map {
                KeyDiff(
                    KeyWithHash(it, ""),
                    KeyAction.ADD,
                    DiffSource.ANDROID
                )
            }

        val manifestFavoritesWithEmptyHash = manifestFile.favorites.map { KeyWithHash(it, "") }
        val favoritesWithEmptyHash = favorites.map { KeyWithHash(it, "") }

        return compare(
            manifestFavoritesWithEmptyHash,
            favoritesWithEmptyHash,
            DiffSource.ANDROID
        )
    }

    override suspend fun compareFlipperFavoritesWithManifest(
        favoritesOnFlipper: List<FlipperFilePath>
    ): List<KeyDiff> {
        val manifestFile = manifestStorage.load()
            ?: return favoritesOnFlipper.map {
                KeyDiff(
                    KeyWithHash(it, ""),
                    KeyAction.ADD,
                    DiffSource.FLIPPER
                )
            }

        val manifestFavoritesWithEmptyHash =
            manifestFile.favoritesFromFlipper.map { KeyWithHash(it, "") }
        val favoritesWithEmptyHash = favoritesOnFlipper.map { KeyWithHash(it, "") }

        return compare(
            manifestFavoritesWithEmptyHash,
            favoritesWithEmptyHash,
            DiffSource.FLIPPER
        )
    }

    override suspend fun getFavorites() = manifestStorage.load()?.favorites
}

/**
 * Returns how target differs from source
 *
 * Respect order
 */
private fun compare(
    source: List<KeyWithHash>,
    target: List<KeyWithHash>,
    diffSource: DiffSource
): List<KeyDiff> {
    val diff = mutableListOf<KeyDiff>()
    val manifestKeys = source.map {
        it.keyPath to it
    }.toMap(LinkedHashMap(source.size))

    for (key in target) {
        val keyInManifest = manifestKeys.remove(key.keyPath)
        if (keyInManifest == null) {
            // If key is new for us
            diff.add(KeyDiff(key, KeyAction.ADD, diffSource))
        } else if (keyInManifest.hash != key.hash) {
            // If key exist, but content is different
            diff.add(KeyDiff(key, KeyAction.MODIFIED, diffSource))
        }
    }

    // Add all unpresent keys as deleted
    manifestKeys.values.forEach {
        diff.add(KeyDiff(it, KeyAction.DELETED, diffSource))
    }

    return diff
}
