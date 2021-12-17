package com.flipperdevices.bridge.dao.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flipperdevices.bridge.dao.impl.converters.FlipperFileTypeConverter
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.repository.KeyDao

@Database(entities = [Key::class], version = 1)
@TypeConverters(FlipperFileTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keyDao(): KeyDao
}
