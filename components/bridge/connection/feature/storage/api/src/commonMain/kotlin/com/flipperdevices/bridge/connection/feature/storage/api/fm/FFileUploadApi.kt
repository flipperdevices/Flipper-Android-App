package com.flipperdevices.bridge.connection.feature.storage.api.fm

import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.core.progress.FixedProgressListener
import okio.Path
import okio.Sink

interface FFileUploadApi {
    suspend fun upload(
        pathOnFlipper: String,
        fileOnAndroid: Path,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT,
        progressListener: FixedProgressListener? = null
    ): Result<Unit>

    suspend fun mkdir(
        pathOnFlipper: String
    ): Result<Unit>

    suspend fun sink(
        pathOnFlipper: String,
        priority: StorageRequestPriority = StorageRequestPriority.DEFAULT
    ): Sink

    suspend fun move(
        oldPath: Path,
        newPath: Path,
    ): Result<Unit>
}
