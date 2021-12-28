package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.model.Key
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_keys JOIN keys ON favorite_keys.key_id = keys.uid")
    fun subscribe(): Flow<Map<FavoriteKey, Key>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorites: List<FavoriteKey>)

    @Query("DELETE FROM favorite_keys")
    fun deleteAll()
}
