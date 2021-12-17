package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.impl.model.Key
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyDao {
    @Query("SELECT * FROM keys")
    suspend fun getAll(): List<Key>

    @Query("SELECT * FROM keys")
    fun subscribe(): Flow<List<Key>>

    @Query("SELECT * FROM keys WHERE type = :fileType")
    fun subscribeByType(fileType: FlipperFileType): Flow<List<Key>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg keys: Key)
}
