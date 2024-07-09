package com.flipperdevices.bridge.dao.impl.api

import androidx.room.withTransaction
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.ktx.getFlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class)
class FavoriteImpl @Inject constructor(
    favoriteDaoProvider: Provider<FavoriteDao>,
    keyDaoProvider: Provider<KeyDao>,
    databaseProvider: Provider<AppDatabase>
) : FavoriteApi {
    private val favoriteDao by favoriteDaoProvider
    private val keyDao by keyDaoProvider
    private val database by databaseProvider

    override suspend fun updateFavorites(
        keys: List<FlipperKeyPath>
    ): List<FlipperKeyPath> = withContext(FlipperDispatchers.workStealingDispatcher) {
        database.withTransaction {
            favoriteDao.deleteAll()

            val flipperKeys = keys.mapNotNull {
                val fileType = it.path.keyType
                return@mapNotNull if (fileType != null) {
                    keyDao.getByPath(it.path.pathToKey, it.deleted)
                } else {
                    null
                }
            }

            val favoriteKeys = flipperKeys.map { it.uid }.mapIndexed { order, keyId ->
                FavoriteKey(keyId = keyId, order = order)
            }

            favoriteDao.insert(favoriteKeys)
            return@withTransaction flipperKeys
        }.map { it.getFlipperKeyPath() }
    }

    override suspend fun isFavorite(keyPath: FlipperKeyPath) =
        withContext(FlipperDispatchers.workStealingDispatcher) {
            val favoritesResponse = favoriteDao.isFavorite(keyPath.path.pathToKey, keyPath.deleted)
            return@withContext favoritesResponse.isNotEmpty()
        }

    override suspend fun setFavorite(
        keyPath: FlipperKeyPath,
        isFavorite: Boolean
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
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

    override fun getFavoritesFlow(): Flow<List<FlipperKey>> {
        return favoriteDao.subscribe().map { keys ->
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

    override suspend fun getFavorites(): List<FlipperKey> =
        withContext(FlipperDispatchers.workStealingDispatcher) {
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
