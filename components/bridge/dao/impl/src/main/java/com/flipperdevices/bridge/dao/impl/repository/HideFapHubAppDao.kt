package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.flipperdevices.bridge.dao.impl.model.HideFapHubApp
import kotlinx.coroutines.flow.Flow

@Dao
interface HideFapHubAppDao {
    @Query("SELECT * FROM faphub_hide_app")
    fun fetchAllHideFapHub(): Flow<List<HideFapHubApp>>

    @Query("SELECT * FROM faphub_hide_app WHERE app_uid = :applicationUid")
    suspend fun getOneItem(applicationUid: String): HideFapHubApp?

    @Delete
    suspend fun delete(fapHubApp: HideFapHubApp)

    @Insert
    suspend fun insert(fapHubApp: HideFapHubApp)
}
