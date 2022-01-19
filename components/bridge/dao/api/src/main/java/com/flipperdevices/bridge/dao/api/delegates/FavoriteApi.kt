package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface FavoriteApi {
    suspend fun updateFavorites(keys: List<FlipperKeyPath>)

    suspend fun getFavoritesFlow(): Flow<List<FlipperKey>>

    suspend fun getFavorites(): List<FlipperKey>
}
