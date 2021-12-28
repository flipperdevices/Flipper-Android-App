package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import kotlinx.coroutines.flow.Flow

interface FavoriteApi {
    suspend fun updateFavorites(keys: List<FlipperKey>)

    suspend fun getFavoritesFlow(): Flow<List<FlipperKey>>
}
