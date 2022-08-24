package com.flipperdevices.bridge.dao.impl.di

import android.content.Context
import androidx.room.Room
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.converters.DatabaseKeyContentConverter
import com.flipperdevices.bridge.dao.impl.repository.KeyDao
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
class RoomDatabaseModule {
    @Provides
    @Singleton
    fun provideRoom(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).addTypeConverter(DatabaseKeyContentConverter(context))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAdditionFileDao(database: AppDatabase) = database.additionalFileDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase) = database.favoriteDao()

    @Provides
    fun provideKeyDao(database: AppDatabase) = database.keyDao()

    @Provides
    fun provideDeleteKeyDao(keyDao: KeyDao): DeleteKeyDao = keyDao

    @Provides
    fun provideSimpleKeyDao(keyDao: KeyDao): SimpleKeyDao = keyDao

    @Provides
    fun provideUtilsKeyDao(keyDao: KeyDao): UtilsKeyDao = keyDao
}
