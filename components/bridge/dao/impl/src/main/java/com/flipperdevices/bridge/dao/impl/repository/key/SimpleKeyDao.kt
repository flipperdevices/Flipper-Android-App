package com.flipperdevices.bridge.dao.impl.repository.key

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.impl.model.Key
import kotlinx.coroutines.flow.Flow

interface SimpleKeyDao {
    @Query("SELECT * FROM keys WHERE deleted = 0")
    suspend fun getAll(): List<Key>

    @Query("SELECT * FROM keys WHERE deleted = 0 ")
    fun subscribe(): Flow<List<Key>>

    @Query("SELECT * FROM keys WHERE path = :path AND deleted = :deleted")
    suspend fun getByPath(path: String, deleted: Boolean): Key?

    @Query("SELECT * FROM keys WHERE path = :path AND deleted = :deleted")
    fun getByPathFlow(path: String, deleted: Boolean): Flow<Key?>

    @Query(
        """
       SELECT * FROM keys WHERE type = :fileType AND deleted = 0 
        """
    )
    fun subscribeByType(fileType: FlipperFileType): Flow<List<Key>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg keys: Key)
}
