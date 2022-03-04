package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface KeyApi {
    suspend fun getAllKeys(): List<FlipperKey>

    suspend fun insertKey(key: FlipperKey)

    suspend fun deleteMarkedDeleted(keyPath: FlipperKeyPath)

    suspend fun markDeleted(keyPath: FlipperKeyPath)

    suspend fun updateNote(keyPath: FlipperKeyPath, note: String)

    suspend fun getKey(keyPath: FlipperKeyPath): FlipperKey?

    /**
     * We try to find the nearest name that has no conflict.
     * Example:
     * Conflict with path "nfc/My_card.nfc"
     * Try "nfc/My_card_1.nfc"... Failed
     * Try "nfc/My_card_2.nfc"... Success!
     */
    suspend fun findAvailablePath(keyPath: FlipperKeyPath): FlipperKeyPath

    fun getExistKeysAsFlow(
        fileType: FlipperFileType? = null
    ): Flow<List<FlipperKey>>
}
