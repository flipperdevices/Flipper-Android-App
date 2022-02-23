package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.api.delegates.KeyContentCleaner
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
@ContributesBinding(AppGraph::class)
class KeyApiImpl @Inject constructor(
    keysDaoProvider: Provider<KeyDao>,
    cleanerProvider: Provider<KeyContentCleaner>
) : KeyApi {
    private val keysDao by keysDaoProvider
    private val cleaner by cleanerProvider

    override suspend fun insertKey(key: FlipperKey) {
        keysDao.insert(key.toDatabaseKey())
    }

    override suspend fun markDeleted(keyPath: FlipperKeyPath) {
        keysDao.markDeleted(keyPath)
    }

    override suspend fun getKey(keyPath: FlipperKeyPath): FlipperKey? {
        return keysDao.getByPath(keyPath)?.toFlipperKey()
    }

    override fun getExistKeysAsFlow(
        fileType: FlipperFileType?
    ): Flow<List<FlipperKey>> {
        val flowWithFilter = if (fileType == null) {
            keysDao.subscribe()
        } else keysDao.subscribeByType(fileType)
        return flowWithFilter.map { keys ->
            keys.map { it.toFlipperKey() }
        }
    }
}

private fun FlipperKey.toDatabaseKey(): Key {
    return Key(
        path = path,
        content = DatabaseKeyContent(keyContent)
    )
}

private fun Key.toFlipperKey(): FlipperKey {
    return FlipperKey(
        path = path,
        keyContent = content.flipperContent
    )
}
