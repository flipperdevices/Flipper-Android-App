package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.squareup.anvil.annotations.ContributesBinding
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
            val fileType = it.path.keyType
            return@mapNotNull if (fileType != null) {
                keyDao.getByPath(it.path.pathToKey, it.deleted)?.uid
            } else null
        }.mapIndexed { order, keyId ->
            FavoriteKey(keyId = keyId, order = order)
        }

        favoriteDao.insert(favoriteKeys)
    }

    override suspend fun isFavorite(keyPath: FlipperKeyPath) = withContext(Dispatchers.IO) {
        val favoritesResponse = favoriteDao.isFavorite(keyPath.path.pathToKey, keyPath.deleted)
        return@withContext favoritesResponse.isNotEmpty()
    }

    override suspend fun setFavorite(
        keyPath: FlipperKeyPath,
        isFavorite: Boolean
    ) = withContext(Dispatchers.IO) {
        val uid =
            keyDao.getByPath(keyPath.path.pathToKey, keyPath.deleted)?.uid ?: return@withContext
        val favoriteKey = favoriteDao.getFavoriteByKeyId(uid)
        if (favoriteKey != null) {
            if (!isFavorite) {
                favoriteDao.delete(favoriteKey)
            } // else do nothing
            return@withContext
        }

        favoriteDao.insert(
            listOf(
                FavoriteKey(
                    keyId = uid,
                    order = favoriteDao.maxOrderCount() + 1
                )
            )
        )
    }

    override suspend fun getFavoritesFlow(): Flow<List<FlipperKey>> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.subscribe().map { keys ->
            keys.filter { !it.value.deleted }.map { (favoriteKey, key) ->
                favoriteKey.order to FlipperKey(
                    mainFile = FlipperFile(
                        path = key.mainFilePath,
                        content = key.content.flipperContent
                    ),
                    notes = key.notes,
                    synchronized = key.synchronizedStatus == SynchronizedStatus.SYNCHRONIZED,
                    deleted = key.deleted
                )
            }.sortedBy { (order, _) -> order }.map { it.second }
        }
    }

    override suspend fun getFavorites(): List<FlipperKey> = withContext(Dispatchers.IO) {
        return@withContext favoriteDao.getAll()
            .filter { !it.value.deleted }
            .map { (favoriteKey, key) ->
                favoriteKey.order to FlipperKey(
                    mainFile = FlipperFile(
                        path = key.mainFilePath,
                        content = key.content.flipperContent
                    ),
                    notes = key.notes,
                    synchronized = key.synchronizedStatus == SynchronizedStatus.SYNCHRONIZED,
                    deleted = key.deleted
                )
            }.sortedBy { (order, _) -> order }.map { it.second }
    }
}
