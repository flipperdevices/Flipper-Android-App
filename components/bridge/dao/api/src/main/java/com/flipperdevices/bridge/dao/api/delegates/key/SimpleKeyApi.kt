package com.flipperdevices.bridge.dao.api.delegates.key

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import kotlinx.coroutines.flow.Flow

interface SimpleKeyApi {
    suspend fun getAllKeys(): List<FlipperKey>

    fun getKeyAsFlow(keyPath: FlipperKeyPath): Flow<FlipperKey?>

    fun getExistKeysAsFlow(
        fileType: FlipperKeyType? = null
    ): Flow<List<FlipperKey>>

    suspend fun insertKey(key: FlipperKey)

    suspend fun updateKeyContent(keyPath: FlipperKeyPath, content: FlipperKeyContent)

    suspend fun getKey(keyPath: FlipperKeyPath): FlipperKey?
}
