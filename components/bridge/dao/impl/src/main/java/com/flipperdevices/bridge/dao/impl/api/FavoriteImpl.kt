package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
@ContributesBinding(AppGraph::class)
class FavoriteImpl @Inject constructor(
    favoriteDaoProvider: Provider<FavoriteDao>,
    keyDaoProvider: Provider<KeyDao>
) : FavoriteApi {
    private val favoriteDao by favoriteDaoProvider
    private val keyDao by keyDaoProvider

    override suspend fun updateFavorites(
        keys: List<FlipperKeyPath>
    ) = withContext(Dispatchers.IO) {
        favoriteDao.deleteAll()

        val favoriteKeys = keys.mapNotNull {
            val fileType = it.fileType
            return@mapNotNull if (fileType != null) {
                keyDao.getByPath(it)?.uid
            } else null
        }.mapIndexed { order, keyId ->
            FavoriteKey(keyId = keyId, order = order)
        }

        favoriteDao.insert(favoriteKeys)
    }

    override suspend fun getFavoritesFlow(): Flow<List<FlipperKey>> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.subscribe().map { keys ->
            keys.map { (favoriteKey, key) ->
                favoriteKey.order to FlipperKey(
                    key.path,
                    FlipperKeyContent.InternalFile(File(key.filePath))
                )
            }.sortedBy { (order, _) -> order }.map { it.second }
        }
    }
}
