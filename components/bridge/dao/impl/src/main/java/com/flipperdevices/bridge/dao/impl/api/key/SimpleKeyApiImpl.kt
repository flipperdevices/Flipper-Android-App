package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.ktx.toDatabaseKey
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperKey
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, SimpleKeyApi::class)
class SimpleKeyApiImpl @Inject constructor(
    keysDaoProvider: Provider<SimpleKeyDao>,
    favoriteApiProvider: Provider<FavoriteApi>
) : SimpleKeyApi, LogTagProvider {
    override val TAG = "SimpleKeyApi"

    private val simpleKeyDao by keysDaoProvider
    private val favoriteApi by favoriteApiProvider

    override suspend fun getAllKeys(): List<FlipperKey> = withContext(Dispatchers.IO) {
        return@withContext simpleKeyDao.getAll().map { it.toFlipperKey() }
    }

    override fun getKeyAsFlow(keyPath: FlipperKeyPath): Flow<FlipperKey?> {
        return simpleKeyDao.getByPathFlow(keyPath.pathToKey, keyPath.deleted).map {
            it?.toFlipperKey()
        }
    }

    override fun getExistKeysAsFlow(
        fileType: FlipperFileType?
    ): Flow<List<FlipperKey>> {
        val flowWithFilter = if (fileType == null) {
            simpleKeyDao.subscribe()
        } else simpleKeyDao.subscribeByType(fileType)
        return flowWithFilter.map { keys ->
            keys.map { it.toFlipperKey() }
        }
    }

    override suspend fun insertKey(key: FlipperKey) = withContext(Dispatchers.IO) {
        simpleKeyDao.insert(key.toDatabaseKey())
    }

    override suspend fun updateKey(
        oldKey: FlipperKey,
        newKey: FlipperKey
    ) = withContext(Dispatchers.IO) {
        if (oldKey == newKey) return@withContext

        val oldKeyInDatabase = simpleKeyDao.getByPath(oldKey.path.pathToKey, deleted = false)
            ?: throw IllegalArgumentException("Can't find old key in database")

        val newKeyInDatabase = newKey.toDatabaseKey().copy(
            uid = oldKeyInDatabase.uid
        )
        simpleKeyDao.update(newKeyInDatabase)
    }

    override suspend fun getKey(
        keyPath: FlipperKeyPath
    ): FlipperKey? = withContext(Dispatchers.IO) {
        return@withContext simpleKeyDao.getByPath(keyPath.pathToKey, keyPath.deleted)
            ?.toFlipperKey()
    }
}
