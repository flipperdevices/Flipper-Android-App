package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.Key
import kotlinx.coroutines.flow.Flow

/**
 * By default all method exclude deleted field
 */
@Dao
interface KeyDao {
    @Query("SELECT * FROM keys WHERE deleted = 0")
    suspend fun getAll(): List<Key>

    @Query("SELECT * FROM keys WHERE deleted = 1")
    fun subscribeOnDeletedKeys(): Flow<List<Key>>

    @Query("SELECT * FROM keys WHERE deleted = 0 ")
    fun subscribe(): Flow<List<Key>>

    @Query(
        """
       SELECT * FROM keys WHERE type = :fileType AND deleted = 0 
        """
    )
    fun subscribeByType(fileType: FlipperFileType): Flow<List<Key>>

    @Query("SELECT * FROM keys WHERE path = :keyPath AND deleted = :deleted")
    suspend fun getByPath(keyPath: FlipperKeyPath, deleted: Boolean = false): Key?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg keys: Key)

    @Query("DELETE FROM keys WHERE path = :keyPath AND deleted = 1")
    suspend fun deleteMarkedDeleted(keyPath: FlipperKeyPath)

    @Query("UPDATE keys SET notes = :note WHERE path = :keyPath AND deleted = 0")
    suspend fun updateNote(keyPath: FlipperKeyPath, note: String)

    @Query("UPDATE keys SET deleted = 1 WHERE path = :keyPath AND deleted = 0")
    suspend fun markDeleted(keyPath: FlipperKeyPath)
}
