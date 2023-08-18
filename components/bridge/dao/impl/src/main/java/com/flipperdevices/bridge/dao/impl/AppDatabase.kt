package com.flipperdevices.bridge.dao.impl

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.converters.FlipperFileTypeConverter
import com.flipperdevices.bridge.dao.impl.model.FavoriteKey
import com.flipperdevices.bridge.dao.impl.model.FlipperAdditionalFile
import com.flipperdevices.bridge.dao.impl.model.HideFapHubApp
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.WidgetDataElement
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.HideFapHubAppDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.bridge.dao.impl.repository.WidgetDataDao

@Database(
    entities = [
        Key::class,
        FavoriteKey::class,
        FlipperAdditionalFile::class,
        WidgetDataElement::class,
        HideFapHubApp::class
    ],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
        // From 2 to 4 we just drop database
        AutoMigration(
            from = 4,
            to = 5
        ),
        // From 5 to 6 we just drop database
        AutoMigration(
            from = 6,
            to = 7
        ),
        AutoMigration(
            from = 7,
            to = 8
        ),
        AutoMigration(
            from = 8,
            to = 9
        ),
        AutoMigration(
            from = 9,
            to = 10
        )
    ],
    version = 10,
    exportSchema = true
)
@TypeConverters(
    FlipperFileTypeConverter::class,
    DatabaseKeyContentConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keyDao(): KeyDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun additionalFileDao(): AdditionalFileDao
    abstract fun widgetDataDao(): WidgetDataDao
    abstract fun fapHubHideApp(): HideFapHubAppDao
}
