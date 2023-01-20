package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.impl.ktx.toDatabaseKey
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperKey
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, SimpleKeyApi::class)
class SimpleKeyApiImpl @Inject constructor(
    keysDaoProvider: Provider<SimpleKeyDao>,
    additionalFileDaoProvider: Provider<AdditionalFileDao>
) : SimpleKeyApi, LogTagProvider {
    override val TAG = "SimpleKeyApi"

    private val simpleKeyDao by keysDaoProvider
    private val additionalFileDao by additionalFileDaoProvider

    override suspend fun getAllKeys(
        includeDeleted: Boolean
    ): List<FlipperKey> = withContext(Dispatchers.IO) {
        return@withContext if (includeDeleted) {
            simpleKeyDao.getAllIncludeDeleted()
        } else {
            simpleKeyDao.getAll()
        }.map { it.toFlipperKey(additionalFileDao) }
    }

    override suspend fun getExistKeys(
        fileType: FlipperKeyType?
    ): List<FlipperKey> = withContext(Dispatchers.IO) {
        return@withContext if (fileType == null) {
            simpleKeyDao.getAll()
        } else {
            simpleKeyDao.getByType(fileType)
        }.map { it.toFlipperKey(additionalFileDao) }
    }

    override fun getKeyAsFlow(keyPath: FlipperKeyPath): Flow<FlipperKey?> {
        return simpleKeyDao.getByPathFlow(keyPath.path.pathToKey, keyPath.deleted).map {
            it?.toFlipperKey(additionalFileDao)
        }
    }

    override fun getExistKeysAsFlow(
        fileType: FlipperKeyType?
    ): Flow<List<FlipperKey>> {
        val flowWithFilter = if (fileType == null) {
            simpleKeyDao.subscribe()
        } else {
            simpleKeyDao.subscribeByType(fileType)
        }
        return flowWithFilter.map { keys ->
            keys.map { it.toFlipperKey(additionalFileDao) }
        }
    }

    override suspend fun insertKey(key: FlipperKey) = withContext(Dispatchers.IO) {
        simpleKeyDao.insert(key.toDatabaseKey())
    }

    override suspend fun updateKeyContent(keyPath: FlipperKeyPath, content: FlipperKeyContent) {
        val existedKey = simpleKeyDao.getByPath(keyPath.path.pathToKey, keyPath.deleted)
            ?: error("Can't find $keyPath")
        simpleKeyDao.update(existedKey.copy(content = DatabaseKeyContent(content)))
    }

    override suspend fun getKey(
        keyPath: FlipperKeyPath
    ): FlipperKey? = withContext(Dispatchers.IO) {
        return@withContext simpleKeyDao.getByPath(keyPath.path.pathToKey, keyPath.deleted)
            ?.toFlipperKey(additionalFileDao)
    }
}
