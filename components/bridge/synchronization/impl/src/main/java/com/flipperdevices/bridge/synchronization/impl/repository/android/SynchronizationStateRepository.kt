package com.flipperdevices.bridge.synchronization.impl.repository.android

import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error

class SynchronizationStateRepository(
    private val utilsKeyApi: UtilsKeyApi
) : LogTagProvider {
    override val TAG = "SynchronizationStateRepository"

    suspend fun markAsSynchronized(usedKeys: List<FlipperKey>) {
        usedKeys.forEach {
            try {
                utilsKeyApi.markAsSynchronized(it.getKeyPath())
            } catch (throwable: Exception) {
                error(throwable) { "Error while marked as synchronized" }
            }
        }
    }
}
