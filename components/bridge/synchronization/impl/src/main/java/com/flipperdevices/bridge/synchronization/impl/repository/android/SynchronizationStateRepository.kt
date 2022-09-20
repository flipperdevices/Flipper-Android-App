package com.flipperdevices.bridge.synchronization.impl.repository.android

import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface SynchronizationStateRepository {
    suspend fun markAsSynchronized(usedKeys: List<FlipperKey>)
}

@ContributesBinding(TaskGraph::class, SynchronizationStateRepository::class)
class SynchronizationStateRepositoryImpl @Inject constructor(
    private val utilsKeyApi: UtilsKeyApi
) : SynchronizationStateRepository, LogTagProvider {
    override val TAG = "SynchronizationStateRepository"

    override suspend fun markAsSynchronized(usedKeys: List<FlipperKey>) {
        usedKeys.forEach {
            try {
                utilsKeyApi.markAsSynchronized(it.getKeyPath())
            } catch (throwable: Exception) {
                error(throwable) { "Error while marked as synchronized" }
            }
        }
    }
}
