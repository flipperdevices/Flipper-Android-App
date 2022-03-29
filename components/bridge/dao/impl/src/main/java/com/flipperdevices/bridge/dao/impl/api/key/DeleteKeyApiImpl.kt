package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.api.delegates.KeyContentCleaner
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperKey
import com.flipperdevices.bridge.dao.impl.repository.key.DeleteKeyDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
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
    cleanerProvider: Provider<KeyContentCleaner>
) : DeleteKeyApi, LogTagProvider {
    override val TAG = "DeleteKeyApi"

    private val deleteKeyDao by deleteKeysDaoProvider
    private val simpleKeyDao by simpleKeysDaoProvider
    private val cleaner by cleanerProvider

    override fun getDeletedKeyAsFlow(): Flow<List<FlipperKey>> {
        return deleteKeyDao.subscribeOnDeletedKeys().map { list ->
            list.map { it.toFlipperKey() }
        }
    }

    override suspend fun deleteMarkedDeleted(
        keyPath: FlipperKeyPath
    ) = withContext(Dispatchers.IO) {
        deleteKeyDao.deleteMarkedDeleted(keyPath)
        cleaner.deleteUnusedFiles()
    }

    override suspend fun markDeleted(keyPath: FlipperKeyPath) = withContext(Dispatchers.IO) {
        val existKey = simpleKeyDao.getByPath(keyPath, deleted = true)
        if (existKey != null) {
            deleteKeyDao.deleteMarkedDeleted(keyPath)
        }
        deleteKeyDao.markDeleted(keyPath)
    }
}
