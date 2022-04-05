package com.flipperdevices.bridge.dao.impl.api.key

import android.database.sqlite.SQLiteConstraintException
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.api.delegates.KeyContentCleaner
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperKey
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, DeleteKeyApi::class)
class DeleteKeyApiImpl @Inject constructor(
    deleteKeysDaoProvider: Provider<DeleteKeyDao>,
    simpleKeysDaoProvider: Provider<SimpleKeyDao>,
    utilsKeyApiProvider: Provider<UtilsKeyApi>,
    cleanerProvider: Provider<KeyContentCleaner>
) : DeleteKeyApi, LogTagProvider {
    override val TAG = "DeleteKeyApi"

    private val deleteKeyDao by deleteKeysDaoProvider
    private val simpleKeyDao by simpleKeysDaoProvider
    private val utilsKeyApi by utilsKeyApiProvider
    private val cleaner by cleanerProvider

    override fun getDeletedKeyAsFlow(): Flow<List<FlipperKey>> {
        return deleteKeyDao.subscribeOnDeletedKeys().map { list ->
            list.map { it.toFlipperKey() }
        }
    }

    override suspend fun deleteMarkedDeleted(
        keyPath: FlipperKeyPath
    ) = withContext(Dispatchers.IO) {
        deleteKeyDao.deleteMarkedDeleted(keyPath.pathToKey)
        cleaner.deleteUnusedFiles()
    }

    override suspend fun markDeleted(keyPath: FlipperKeyPath) = withContext(Dispatchers.IO) {
        val existKey = simpleKeyDao.getByPath(keyPath.pathToKey, deleted = true)
        if (existKey != null) {
            deleteKeyDao.deleteMarkedDeleted(keyPath.pathToKey)
        }
        deleteKeyDao.markDeleted(keyPath.pathToKey)
    }

    override suspend fun restore(keyPath: FlipperKeyPath) {
        var newPath = keyPath.pathToKey
        val existKey = simpleKeyDao.getByPath(newPath, deleted = false)
        if (existKey != null) {
            newPath = utilsKeyApi.findAvailablePath(keyPath.copy(deleted = false)).pathToKey
            try {
                simpleKeyDao.move(keyPath.pathToKey, newPath, keyPath.deleted)
            } catch (constraintException: SQLiteConstraintException) {
                error(constraintException) { "When try restore $keyPath" }
                restore(keyPath)
                return
            }
        }
        deleteKeyDao.restore(newPath)
    }
}
