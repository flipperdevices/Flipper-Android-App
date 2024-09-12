package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.progress.ProgressListener

interface FFileTimestampApi {
    suspend fun fetchFolderTimestamp(
        folder: String,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT
    ): Long?
}