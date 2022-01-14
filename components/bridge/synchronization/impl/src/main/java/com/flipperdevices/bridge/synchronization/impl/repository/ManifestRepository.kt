package com.flipperdevices.bridge.synchronization.impl.repository

import android.content.Context
import com.flipperdevices.bridge.synchronization.impl.di.SynchronizationComponent
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.toLinkedHashMap
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

    suspend fun saveManifest(keys: List<KeyWithHash>) {
        manifestStorage.save(keys)
    }

    suspend fun compareWithManifest(keys: List<KeyWithHash>): List<KeyDiff> {
        val manifestFile = manifestStorage.load()
            ?: return keys.map { KeyDiff(it, KeyAction.ADD) }
        val diff = mutableListOf<KeyDiff>()
        val manifestKeys = manifestFile.keys.toLinkedHashMap()

        for (key in keys) {
            val keyInManifest = manifestKeys.remove(key.keyPath.pathToKey)
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
}
