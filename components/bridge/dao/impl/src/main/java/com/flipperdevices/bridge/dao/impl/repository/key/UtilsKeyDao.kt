package com.flipperdevices.bridge.dao.impl.repository.key

import androidx.room.Query
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import kotlinx.coroutines.flow.Flow

interface UtilsKeyDao {
    @Query(
        """
       UPDATE keys SET synchronized_status = :status WHERE path = :path AND deleted = :deleted 
        """
    )
    suspend fun markSynchronized(
        path: String,
        deleted: Boolean,
        status: SynchronizedStatus
    )

    @Query("SELECT * FROM keys WHERE deleted = 0 AND (path LIKE :query OR notes LIKE :query)")
    fun search(query: String): Flow<List<Key>>

    @Query("UPDATE keys SET notes = :note WHERE path = :path AND deleted = :deleted")
    suspend fun updateNote(path: String, deleted: Boolean, note: String)
}
