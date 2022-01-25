package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.model.Key
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_keys JOIN keys ON favorite_keys.key_id = keys.uid")
    fun subscribe(): Flow<Map<FavoriteKey, Key>>

    @Query("SELECT * FROM favorite_keys JOIN keys ON favorite_keys.key_id = keys.uid")
    fun getAll(): Map<FavoriteKey, Key>

    @Query(
        """
            SELECT * FROM favorite_keys 
            JOIN keys ON favorite_keys.key_id = keys.uid 
            WHERE keys.path = :keyPath
        """
    )
    suspend fun isFavorite(keyPath: FlipperKeyPath): Map<FavoriteKey, Key>

    @Query("SELECT * FROM favorite_keys WHERE key_id = :id")
    suspend fun getFavoriteByKeyId(id: Int): FavoriteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorites: List<FavoriteKey>)

    @Query("SELECT max(`order`) FROM favorite_keys")
    suspend fun maxOrderCount(): Int

    @Delete
    suspend fun delete(favoriteKey: FavoriteKey)

    @Query("DELETE FROM favorite_keys")
    suspend fun deleteAll()
}
