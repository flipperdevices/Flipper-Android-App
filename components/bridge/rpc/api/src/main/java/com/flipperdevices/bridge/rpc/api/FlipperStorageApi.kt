package com.flipperdevices.bridge.rpc.api

import com.flipperdevices.core.progress.ProgressListener
import java.io.File

interface FlipperStorageApi {
    suspend fun mkdirs(path: String)

    suspend fun delete(path: String, recursive: Boolean = false)

    suspend fun upload(pathOnFlipper: String, fileOnAndroid: File, progressListener: ProgressListener)
}
