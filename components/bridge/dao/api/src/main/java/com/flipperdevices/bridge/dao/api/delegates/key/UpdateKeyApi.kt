package com.flipperdevices.bridge.dao.api.delegates.key

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.coroutines.flow.Flow

interface UpdateKeyApi {
    /**
     * Notifies you when the key path has changed. The first object sends the {@param originalPath}
     */
    fun subscribeOnUpdatePath(originalPath: FlipperKeyPath): Flow<FlipperKeyPath>

    /**
     * Calculates the difference between the two keys and updates only the difference between them.
     * If necessary, marks the file as necessary for synchronization.
     */
    suspend fun updateKey(oldKey: FlipperKey, newKey: FlipperKey)
}
