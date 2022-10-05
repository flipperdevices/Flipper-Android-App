package com.flipperdevices.bridge.dao.impl.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flipperdevices.bridge.dao.impl.model.WidgetDataElement

@Dao
interface WidgetDataDao {
    @Query("SELECT * FROM widgets")
    suspend fun getAll(): List<WidgetDataElement>

    @Query("SELECT * FROM widgets WHERE id = :id")
    suspend fun getWidgetDataById(id: Int): WidgetDataElement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(widgetData: WidgetDataElement)

    @Query("DELETE FROM widgets WHERE id = :id")
    suspend fun delete(id: Int)
}