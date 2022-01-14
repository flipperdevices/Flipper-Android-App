package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface KeyApi {
    suspend fun insertKey(key: FlipperKey)

    suspend fun deleteKey(keyPath: FlipperKeyPath)

    suspend fun getKey(keyPath: FlipperKeyPath): FlipperKey?

    fun getKeysAsFlow(
        fileType: FlipperFileType? = null
    ): Flow<List<FlipperKey>>
}
