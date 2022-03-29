package com.flipperdevices.bridge.dao.api.delegates.key

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface DeleteKeyApi {
    fun getDeletedKeyAsFlow(): Flow<List<FlipperKey>>

    suspend fun deleteMarkedDeleted(keyPath: FlipperKeyPath)

    suspend fun markDeleted(keyPath: FlipperKeyPath)
}
