package com.flipperdevices.bridge.synchronization.impl.repository

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.di.SynchronizationComponent
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestStorage
import com.flipperdevices.core.di.ComponentHolder
import javax.inject.Inject

class ManifestRepository {
    @Inject
    lateinit var context: Context

    private val manifestStorage by lazy { ManifestStorage(context) }

    init {
        ComponentHolder.component<SynchronizationComponent>().inject(this)
    }

    suspend fun saveManifest(keys: List<KeyWithHash>, favorites: List<FlipperKeyPath>) {
        manifestStorage.save(keys, favorites)
    }

    suspend fun compareKeysWithManifest(keys: List<KeyWithHash>): List<KeyDiff> {
        val manifestFile = manifestStorage.load()
            ?: return keys.map { KeyDiff(it, KeyAction.ADD) }
        return compare(keys, manifestFile.keys)
    }

    suspend fun compareFavoritesWithManifest(favorites: List<FlipperKeyPath>): List<KeyDiff> {
        val manifestFile = manifestStorage.load()
            ?: return favorites.map { KeyDiff(KeyWithHash(it, ""), KeyAction.ADD) }

        val manifestFavoritesWithEmptyHash = manifestFile.favorites.map { KeyWithHash(it, "") }
        val favoritesWithEmptyHash = favorites.map { KeyWithHash(it, "") }

        return compare(manifestFavoritesWithEmptyHash, favoritesWithEmptyHash)
    }

    suspend fun getFavorites() = manifestStorage.load()?.favorites
}

/**
 * Returns how target differs from source
 *
 * Respect order
 */
private fun compare(source: List<KeyWithHash>, target: List<KeyWithHash>): List<KeyDiff> {
    val diff = mutableListOf<KeyDiff>()
    val manifestKeys = source.map {
        it.keyPath to it
    }.toMap(LinkedHashMap(source.size))

    for (key in target) {
        val keyInManifest = manifestKeys.remove(key.keyPath)
        if (keyInManifest == null) {
            // If key is new for us
            diff.add(KeyDiff(key, KeyAction.ADD))
        } else if (keyInManifest.hash != key.hash) {
            // If key exist, but content is different
            diff.add(KeyDiff(key, KeyAction.MODIFIED))
        }
    }

    // Add all unpresent keys as deleted
    manifestKeys.values.forEach {
        diff.add(KeyDiff(it, KeyAction.DELETED))
    }

    return diff
}
