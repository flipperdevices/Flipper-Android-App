package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.progress.FixedProgressListener
import okio.Path
import okio.Source

interface FFileDownloadApi {
    suspend fun download(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT,
        progressListener: FixedProgressListener? = null
    ): Result<Unit>

    fun source(
        pathOnFlipper: String,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT
    ): Source
}
