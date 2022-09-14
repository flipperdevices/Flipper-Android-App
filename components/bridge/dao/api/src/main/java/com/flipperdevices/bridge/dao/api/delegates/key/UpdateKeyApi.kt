package com.flipperdevices.bridge.dao.api.delegates.key

import com.flipperdevices.bridge.dao.api.model.FlipperKey

interface UpdateKeyApi {
    /**
     * Calculates the difference between the two keys and updates only the difference between them.
     * If necessary, marks the file as necessary for synchronization.
     */
    suspend fun updateKey(oldKey: FlipperKey, newKey: FlipperKey)
}
