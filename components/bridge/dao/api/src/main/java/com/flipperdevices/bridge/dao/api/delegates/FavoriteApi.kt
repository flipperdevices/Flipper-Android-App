package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKey

interface FavoriteApi {
    suspend fun updateFavorites(keys: List<FlipperKey>)
}
