package com.flipperdevices.bridge.dao.impl.di

import android.content.Context
import androidx.room.Room
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.HideFapHubAppDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.bridge.dao.impl.repository.WidgetDataDao
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.UtilsKeyDao
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

private const val DATABASE_NAME = "flipper.db"

@ContributesTo(AppGraph::class)
@Suppress("TooManyFunctions")
interface RoomDatabaseModule {
    @Provides
    @SingleIn(AppGraph::class)
    fun provideRoom(
        context: Context,
        databaseKeyContentConverter: DatabaseKeyContentConverter,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).addTypeConverter(databaseKeyContentConverter)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideAdditionFileDao(database: AppDatabase): AdditionalFileDao {
        return database.additionalFileDao()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun provideKeyDao(database: AppDatabase): KeyDao {
        return database.keyDao()
    }

    @Provides
    fun provideFapHubHideApp(database: AppDatabase): HideFapHubAppDao {
        return database.fapHubHideApp()
    }

    @Provides
    fun provideDeleteKeyDao(keyDao: KeyDao): DeleteKeyDao {
        return keyDao
    }

    @Provides
    fun provideSimpleKeyDao(keyDao: KeyDao): SimpleKeyDao {
        return keyDao
    }

    @Provides
    fun provideUtilsKeyDao(keyDao: KeyDao): UtilsKeyDao {
        return keyDao
    }

    @Provides
    fun provideWidgetDataDao(database: AppDatabase): WidgetDataDao {
        return database.widgetDataDao()
    }
}
