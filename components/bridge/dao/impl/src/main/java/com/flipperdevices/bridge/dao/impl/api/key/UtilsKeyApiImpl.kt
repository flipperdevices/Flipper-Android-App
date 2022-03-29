package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperKey
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import com.flipperdevices.bridge.dao.impl.repository.key.UtilsKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, UtilsKeyApi::class)
class UtilsKeyApiImpl @Inject constructor(
    utilsKeysDaoProvider: Provider<UtilsKeyDao>,
    simpleKeyApiProvider: Provider<SimpleKeyApi>
) : UtilsKeyApi, LogTagProvider {
    override val TAG = "UtilsKeyApi"

    private val utilsKeyDao by utilsKeysDaoProvider
    private val simpleKeyApi by simpleKeyApiProvider

    override suspend fun markAsSynchronized(
        keyPath: FlipperKeyPath,
        deleted: Boolean
    ) = withContext(Dispatchers.IO) {
        utilsKeyDao.markSynchronized(keyPath, deleted, SynchronizedStatus.SYNCHRONIZED)
    }

    override suspend fun updateNote(
        keyPath: FlipperKeyPath,
        note: String
    ) = withContext(Dispatchers.IO) {
        utilsKeyDao.updateNote(keyPath, note)
    }

    override fun search(text: String): Flow<List<FlipperKey>> {
        val searchQuery = "%$text%"
        return utilsKeyDao.search(searchQuery).map { list ->
            list.map { it.toFlipperKey() }
        }
    }

    override suspend fun findAvailablePath(keyPath: FlipperKeyPath): FlipperKeyPath {
        var newNameWithoutExtension = keyPath.nameWithoutExtension
        var newPath = getKeyPathWithDifferentNameWithoutExtension(
            keyPath,
            newNameWithoutExtension
        )
        var index = 1
        info {
            "Start finding free name for path $newPath " +
                "(newNameWithoutExtension=$newNameWithoutExtension)"
        }
        // Find empty key name
        while (simpleKeyApi.getKey(newPath) != null) {
            newNameWithoutExtension = "${keyPath.nameWithoutExtension}_${index++}"
            newPath = getKeyPathWithDifferentNameWithoutExtension(
                keyPath,
                newNameWithoutExtension
            )
            info {
                "Try $newPath ($newNameWithoutExtension)"
            }
        }
        info { "Found free key name! $newPath" }
        return newPath
    }
}

private fun getKeyPathWithDifferentNameWithoutExtension(
    keyPath: FlipperKeyPath,
    nameWithoutExtension: String
): FlipperKeyPath {
    return FlipperKeyPath(
        keyPath.folder,
        "$nameWithoutExtension.${keyPath.name.substringAfterLast('.')}"
    )
}
