package com.flipperdevices.bridge.dao.impl.api.delegates

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteImpl(
    val favoriteDao: FavoriteDao,
    val keyDao: KeyDao
) : FavoriteApi {
    override suspend fun updateFavorites(
        keys: List<FlipperKey>
    ) = withContext(Dispatchers.IO) {
        favoriteDao.deleteAll()

        val favoriteKeys = keys.mapNotNull {
            keyDao.getByTypeAndName(it.fileType, it.name)
        }.map {
            it.uid
        }.mapIndexed { order, key ->
            FavoriteKey(key = key, order = order)
        }

        favoriteDao.insert(favoriteKeys)
    }
}
