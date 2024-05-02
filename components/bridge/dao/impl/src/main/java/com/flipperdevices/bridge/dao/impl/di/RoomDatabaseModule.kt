package com.flipperdevices.bridge.dao.impl.di

import android.content.Context
import androidx.room.Room
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.comparator.DefaultFileComparator
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.md5.MD5Converter
import com.flipperdevices.bridge.dao.impl.md5.MD5ConverterImpl
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProvider
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProviderImpl
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
        databaseKeyContentConverter: DatabaseKeyContentConverter
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).addTypeConverter(databaseKeyContentConverter)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDatabaseKeyContentConverter(
        md5Converter: MD5Converter,
        mD5FileProvider: MD5FileProvider
    ): DatabaseKeyContentConverter = DatabaseKeyContentConverter(
        md5Converter = md5Converter,
        mD5FileProvider = mD5FileProvider
    )

    @Provides
    fun provideMD5FileProvider(
        context: Context,
        fileComparator: FileComparator
    ): MD5FileProvider = MD5FileProviderImpl(
        context = context,
        fileComparator = fileComparator
    )

    @Provides
    fun provideFileComparator(): FileComparator {
        return DefaultFileComparator
    }

    @Provides
    fun provideMD5Converter(): MD5Converter {
        return MD5ConverterImpl()
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
