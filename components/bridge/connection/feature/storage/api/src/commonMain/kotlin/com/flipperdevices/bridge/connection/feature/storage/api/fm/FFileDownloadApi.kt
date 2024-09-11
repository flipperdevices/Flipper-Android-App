package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.progress.ProgressListener
import okio.Path

interface FFileDownloadApi {
    suspend fun download(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        progressListener: ProgressListener? = null,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT
    )
}