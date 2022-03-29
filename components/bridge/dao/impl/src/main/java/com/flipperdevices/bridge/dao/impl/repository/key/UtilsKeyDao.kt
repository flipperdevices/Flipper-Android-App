package com.flipperdevices.bridge.dao.impl.repository.key

import androidx.room.Query
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import kotlinx.coroutines.flow.Flow

interface UtilsKeyDao {
    @Query(
        """
       UPDATE keys SET synchronized_status = :status WHERE path = :keyPath AND deleted = :deleted 
        """
    )
    suspend fun markSynchronized(
        keyPath: FlipperKeyPath,
        deleted: Boolean,
        status: SynchronizedStatus
    )

    @Query("SELECT * FROM keys WHERE deleted = 0 AND (path LIKE :query OR notes LIKE :query)")
    fun search(query: String): Flow<List<Key>>

    @Query("UPDATE keys SET notes = :note WHERE path = :keyPath AND deleted = 0")
    suspend fun updateNote(keyPath: FlipperKeyPath, note: String)
}
