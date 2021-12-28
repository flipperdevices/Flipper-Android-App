package com.flipperdevices.bridge.dao.impl.api.delegates

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteImpl(
    private val favoriteDao: FavoriteDao,
    private val keyDao: KeyDao
) : FavoriteApi {
    override suspend fun updateFavorites(
        keys: List<FlipperKey>
    ) = withContext(Dispatchers.IO) {
        favoriteDao.deleteAll()

        val favoriteKeys = keys.mapNotNull {
            keyDao.getByTypeAndName(it.fileType, it.name)
        }.map {
            it.uid
        }.mapIndexed { order, keyId ->
            FavoriteKey(keyId = keyId, order = order)
        }

        favoriteDao.insert(favoriteKeys)
    }

    override suspend fun getFavoritesFlow(): Flow<List<FlipperKey>> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.subscribe().map { keys ->
            keys.map { (favoriteKey, key) ->
                favoriteKey.order to FlipperKey(key.name, key.fileType)
            }.sortedBy { (order, _) -> order }.map { it.second }
        }
    }
}
