package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.api.delegates.FlipperKeyContentConverter
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
@ContributesBinding(AppGraph::class)
class KeyApiImpl @Inject constructor(
    keysDaoProvider: Provider<KeyDao>,
    keyContentConverterProvider: Provider<FlipperKeyContentConverter>
) : KeyApi {
    private val keysDao by keysDaoProvider
    private val keyContentConverter by keyContentConverterProvider

    override suspend fun getAllKeys(): List<FlipperKey> {
        return keysDao.getAll().map { it.toFlipperKey() }
    }

    override suspend fun insertKey(key: FlipperKey) {
        val file = keyContentConverter.extractFile(key)
        val databaseKey = key.toDatabaseKey(file)

        keysDao.insert(databaseKey)
    }

    override suspend fun deleteKey(keyPath: FlipperKeyPath) {
        val key = keysDao.getByPath(keyPath) ?: return
        val file = keyContentConverter.extractFile(key.toFlipperKey())
        file.delete()
        keysDao.deleteByPath(keyPath)
    }

    override suspend fun getKey(keyPath: FlipperKeyPath): FlipperKey? {
        return keysDao.getByPath(keyPath)?.toFlipperKey()
    }

    override fun getKeysAsFlow(
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

private fun FlipperKey.toDatabaseKey(keyContent: File): Key {
    return Key(
        path = path,
        filePath = keyContent.absolutePath
    )
}

private fun Key.toFlipperKey(): FlipperKey {
    return FlipperKey(
        path = path,
        keyContent = FlipperKeyContent.InternalFile(File(this.filePath))
    )
}
