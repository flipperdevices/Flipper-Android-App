package com.flipperdevices.bridge.dao.impl.di

import android.content.Context
import androidx.room.Room
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.converters.MD5Converter
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.HideFapHubAppDao
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
import com.flipperdevices.bridge.dao.impl.repository.WidgetDataDao
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.UtilsKeyDao
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

private const val DATABASE_NAME = "flipper.db"

@Module
@ContributesTo(AppGraph::class)
@Suppress("TooManyFunctions")
class RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideRoom(
        context: Context,
        md5Converter: MD5Converter,
        fileComparator: FileComparator
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).addTypeConverter(DatabaseKeyContentConverter(context, md5Converter, fileComparator))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAdditionFileDao(database: AppDatabase): AdditionalFileDao {
        return database.additionalFileDao()
    }

    @Provides
    fun provideFileComparator(): FileComparator {
        return DefaultFileComparator
    }

    @Provides
    fun provideMD5Converter(): MD5Converter {
        return MD5Converter.Default
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
