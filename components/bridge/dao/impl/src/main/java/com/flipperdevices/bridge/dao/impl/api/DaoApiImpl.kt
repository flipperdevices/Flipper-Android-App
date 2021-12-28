package com.flipperdevices.bridge.dao.impl.api

import android.content.Context
import androidx.room.Room
import com.flipperdevices.bridge.dao.api.DaoApi
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.api.delegates.FavoriteImpl
import com.flipperdevices.bridge.dao.impl.api.delegates.KeyApiImpl
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val DATABASE_NAME = "flipper.db"

@ContributesBinding(AppGraph::class)
@Singleton
class DaoApiImpl @Inject constructor(context: Context) : DaoApi {
    private val roomDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    override suspend fun getKeysApi(): KeyApi = withContext(Dispatchers.IO) {
        return@withContext KeyApiImpl(roomDatabase.keyDao())
    }

    override suspend fun getFavoriteApi(): FavoriteApi = withContext(Dispatchers.IO) {
        return@withContext FavoriteImpl(roomDatabase.favoriteDao(), roomDatabase.keyDao())
    }
}
