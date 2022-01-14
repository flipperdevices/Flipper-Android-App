package com.flipperdevices.bridge.dao.impl

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flipperdevices.bridge.dao.impl.converters.FlipperFileTypeConverter
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao

@Database(
    entities = [
        Key::class,
        FavoriteKey::class
    ],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        )
        // From 2 to 3 we just drop database
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(FlipperFileTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keyDao(): KeyDao
    abstract fun favoriteDao(): FavoriteDao
}
