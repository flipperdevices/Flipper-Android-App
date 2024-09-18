package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.progress.FixedProgressListener
import com.flipperdevices.core.progress.ProgressListener
import okio.Path
import okio.Sink

interface FFileUploadApi {
    suspend fun upload(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT,
        progressListener: FixedProgressListener? = null
    ): Result<Unit>

    suspend fun sink(
        pathOnFlipper: String,
        priority: StorageRequestPriority
    ): Sink
}
