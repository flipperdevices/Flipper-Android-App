package com.flipperdevices.keyscreen.impl.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import javax.inject.Inject

class SaveDelegate {
    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    suspend fun onEditSaveInternal(oldKey: FlipperKey, newKey: FlipperKey) {
        val newNote = newKey.notes
        if (oldKey.path == newKey.path && !newNote.isNullOrBlank()) {
            keyApi.updateNote(oldKey.path, newNote)
            return
        }

        if (keyApi.getKey(newKey.path) != null) {
            throw IllegalArgumentException("Key already exist")
        }

        val isFavorite = favoriteApi.isFavorite(oldKey.path)
        if (isFavorite) {
            // Delete key from favorite, because we can't delete it
            favoriteApi.setFavorite(oldKey.path, false)
        }
        keyApi.markDeleted(oldKey.path)
        keyApi.insertKey(newKey)
        if (isFavorite) {
            favoriteApi.setFavorite(newKey.path, true)
        }
    }
}
