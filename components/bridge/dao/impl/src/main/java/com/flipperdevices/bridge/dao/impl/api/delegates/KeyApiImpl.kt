package com.flipperdevices.bridge.dao.impl.api.delegates

import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeyApiImpl(private val keysDao: KeyDao) : KeyApi {
    override suspend fun updateKeys(keys: List<FlipperKey>) {
        keysDao.insertAll(keys.map { it.toDatabaseKey() })
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

private fun FlipperKey.toDatabaseKey(): Key {
    return Key(
        name = this.name,
        fileType = this.fileType
    )
}

private fun Key.toFlipperKey(): FlipperKey {
    return FlipperKey(
        name = this.name,
        fileType = this.fileType
    )
}
