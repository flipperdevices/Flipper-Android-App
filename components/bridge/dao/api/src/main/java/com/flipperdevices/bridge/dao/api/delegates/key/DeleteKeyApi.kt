package com.flipperdevices.bridge.dao.api.delegates.key

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import kotlinx.coroutines.flow.Flow

interface DeleteKeyApi {
    fun getDeletedKeyAsFlow(): Flow<List<FlipperKey>>

    suspend fun deleteMarkedDeleted(keyPath: FlipperFilePath)

    suspend fun markDeleted(keyPath: FlipperFilePath)

    suspend fun restore(keyPath: FlipperFilePath)
}
