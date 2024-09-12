package com.flipperdevices.bridge.connection.feature.storage.impl.fm.timestamp

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileTimestampApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.progress.ProgressListener

class FFileTimestampApiNoop : FFileTimestampApi {
    override suspend fun fetchFolderTimestamp(
        folder: String,
        priority: StorageRequestPriority
    ): Long? = null
}