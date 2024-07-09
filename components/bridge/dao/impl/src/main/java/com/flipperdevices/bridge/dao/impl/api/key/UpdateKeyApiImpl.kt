package com.flipperdevices.bridge.dao.impl.api.key

import androidx.room.withTransaction
import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.AppDatabase
import com.flipperdevices.bridge.dao.impl.api.key.ShouldSynchronizeHelper.isShouldSynchronize
import com.flipperdevices.bridge.dao.impl.ktx.toDatabaseKey
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withFirstElement
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, UpdateKeyApi::class)
class UpdateKeyApiImpl @Inject constructor(
    keysDaoProvider: Provider<SimpleKeyDao>,
    additionalFileApiProvider: Provider<FlipperFileApi>,
    databaseProvider: Provider<AppDatabase>
) : UpdateKeyApi, LogTagProvider {
    override val TAG = "UpdateKeyApi"

    private val simpleKeyDao by keysDaoProvider
    private val additionalFileApi by additionalFileApiProvider
    private val database by databaseProvider

    private val updatePathFlow = MutableSharedFlow<Pair<FlipperKeyPath, FlipperKeyPath>>()

    override fun subscribeOnUpdatePath(originalPath: FlipperKeyPath): Flow<FlipperKeyPath> {
        var lastRememberPath = originalPath
        return updatePathFlow.filter {
            it.first == lastRememberPath
        }.map {
            lastRememberPath = it.second
            return@map it.second
        }.withFirstElement(originalPath)
    }

    override suspend fun updateKey(
        oldKey: FlipperKey,
        newKey: FlipperKey
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (oldKey == newKey) return@withContext

        val isShouldSynchronize = isShouldSynchronize(oldKey, newKey)

        database.withTransaction {
            val oldKeyInDatabase = simpleKeyDao.getByPath(oldKey.path.pathToKey, deleted = false)
                ?: throw IllegalArgumentException("Can't find old key in database")

            val newKeyInDatabase = newKey.toDatabaseKey().copy(
                uid = oldKeyInDatabase.uid,
                synchronizedStatus = if (isShouldSynchronize) {
                    SynchronizedStatus.NOT_SYNCHRONIZED
                } else {
                    SynchronizedStatus.SYNCHRONIZED
                }
            )
            if (oldKey.additionalFiles != newKey.additionalFiles) {
                additionalFileApi.updateAdditionalFiles(
                    oldKeyInDatabase.uid,
                    newKey.additionalFiles
                )
            }
            if (oldKey.path != newKey.path) {
                additionalFileApi.renameAdditionalFiles(oldKeyInDatabase.uid, newKey)
            }
            simpleKeyDao.update(newKeyInDatabase)
        }
        if (oldKey.getKeyPath() != newKey.getKeyPath()) {
            updatePathFlow.emit(oldKey.getKeyPath() to newKey.getKeyPath())
        }
    }
}
