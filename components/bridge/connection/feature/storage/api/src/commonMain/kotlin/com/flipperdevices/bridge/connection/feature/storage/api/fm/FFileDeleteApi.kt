package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority

interface FFileDeleteApi {
    suspend fun delete(
        path: String,
        recursive: Boolean = false,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT
    ): Result<Unit>
}
