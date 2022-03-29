package com.flipperdevices.keyscreen.impl.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import javax.inject.Inject

class SaveDelegate {
    @Inject
    lateinit var utilsKeyApi: UtilsKeyApi

    @Inject
    lateinit var deleteKeyApi: DeleteKeyApi

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
    }

    suspend fun onEditSaveInternal(oldKey: FlipperKey, newKey: FlipperKey) {
        val newNote = newKey.notes
        if (oldKey.path == newKey.path && !newNote.isNullOrBlank()) {
            utilsKeyApi.updateNote(oldKey.path, newNote)
            return
        }

        if (simpleKeyApi.getKey(newKey.path) != null) {
            throw IllegalArgumentException("Key already exist")
        }

        val isFavorite = favoriteApi.isFavorite(oldKey.path)
        if (isFavorite) {
            // Delete key from favorite, because we can't delete it
            favoriteApi.setFavorite(oldKey.path, false)
        }
        deleteKeyApi.markDeleted(oldKey.path)
        simpleKeyApi.insertKey(newKey)
        if (isFavorite) {
            favoriteApi.setFavorite(newKey.path, true)
        }
    }
}
