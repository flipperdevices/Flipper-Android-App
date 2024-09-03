package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface FavoriteApi {
    suspend fun updateFavorites(keys: List<FlipperKeyPath>): List<FlipperKeyPath>

    suspend fun isFavorite(keyPath: FlipperKeyPath): Boolean

    suspend fun setFavorite(keyPath: FlipperKeyPath, isFavorite: Boolean)

    fun getFavoritesFlow(): Flow<List<FlipperKey>>

    suspend fun getFavorites(): List<FlipperKey>
}
