package com.flipperdevices.bridge.dao.api.delegates.key

import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface SimpleKeyApi {
    suspend fun getAllKeys(): List<FlipperKey>

    fun getKeyAsFlow(keyPath: FlipperKeyPath): Flow<FlipperKey?>

    fun getExistKeysAsFlow(
        fileType: FlipperFileType? = null
    ): Flow<List<FlipperKey>>

    suspend fun insertKey(key: FlipperKey)

    suspend fun updateKey(oldKey: FlipperKey, newKey: FlipperKey)

    suspend fun getKey(keyPath: FlipperKeyPath): FlipperKey?
}
