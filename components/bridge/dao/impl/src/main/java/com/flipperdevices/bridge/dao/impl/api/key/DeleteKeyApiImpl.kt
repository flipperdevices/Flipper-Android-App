package com.flipperdevices.bridge.dao.impl.api.key

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.api.delegates.KeyContentCleaner
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperKey
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import com.flipperdevices.bridge.dao.impl.repository.FavoriteDao
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

@Suppress("LongParameterList")
@ContributesBinding(AppGraph::class, DeleteKeyApi::class)
class DeleteKeyApiImpl @Inject constructor(
    deleteKeysDaoProvider: Provider<DeleteKeyDao>,
    simpleKeysDaoProvider: Provider<SimpleKeyDao>,
    utilsKeyApiProvider: Provider<UtilsKeyApi>,
    cleanerProvider: Provider<KeyContentCleaner>,
    databaseProvider: Provider<AppDatabase>,
    additionalFileDaoProvider: Provider<AdditionalFileDao>,
    favoriteDaoProvider: Provider<FavoriteDao>
) : DeleteKeyApi, LogTagProvider {
    override val TAG = "DeleteKeyApi"

    private val deleteKeyDao by deleteKeysDaoProvider
    private val simpleKeyDao by simpleKeysDaoProvider
    private val utilsKeyApi by utilsKeyApiProvider
    private val cleaner by cleanerProvider
    private val database by databaseProvider
    private val additionalFileDao by additionalFileDaoProvider
    private val favoriteDao by favoriteDaoProvider

    override fun getDeletedKeyAsFlow(): Flow<List<FlipperKey>> {
        return deleteKeyDao.subscribeOnDeletedKeys().map { list ->
            list.map { it.toFlipperKey(additionalFileDao) }
        }
    }

    override suspend fun deleteMarkedDeleted(
        keyPath: FlipperFilePath
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        database.withTransaction {
            val key = simpleKeyDao.getByPath(keyPath.pathToKey, deleted = true)
            if (key != null) {
                val favoriteKey = favoriteDao.getFavoriteByKeyId(key.uid)
                if (favoriteKey != null) {
                    favoriteDao.delete(favoriteKey)
                }
                deleteKeyDao.deleteMarkedDeleted(keyPath.pathToKey)
            }
        }
        cleaner.deleteUnusedFiles()
    }

    override suspend fun markDeleted(keyPath: FlipperFilePath) = withContext(
        FlipperDispatchers.workStealingDispatcher
    ) {
        val existKey = simpleKeyDao.getByPath(keyPath.pathToKey, deleted = true)
        if (existKey != null) {
            deleteMarkedDeleted(keyPath)
        }
        deleteKeyDao.markDeleted(keyPath.pathToKey)
    }

    override suspend fun restore(
        keyPath: FlipperFilePath
    ): Unit = withContext(FlipperDispatchers.workStealingDispatcher) {
        database.withTransaction {
            var newPath = keyPath.pathToKey
            val existKey = simpleKeyDao.getByPath(newPath, deleted = false)
            if (existKey != null) {
                newPath = utilsKeyApi.findAvailablePath(
                    FlipperKeyPath(
                        keyPath,
                        deleted = false
                    )
                ).path.pathToKey
                try {
                    simpleKeyDao.move(keyPath.pathToKey, newPath, deleted = true)
                } catch (constraintException: SQLiteConstraintException) {
                    error(constraintException) { "When try restore $keyPath" }
                    restore(keyPath)
                    return@withTransaction
                }
            }
            deleteKeyDao.restore(newPath)
        }
    }
}
