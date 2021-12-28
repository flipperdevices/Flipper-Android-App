package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorites: List<FavoriteKey>)

    @Query("DELETE FROM favorite_keys")
    fun deleteAll()
}
