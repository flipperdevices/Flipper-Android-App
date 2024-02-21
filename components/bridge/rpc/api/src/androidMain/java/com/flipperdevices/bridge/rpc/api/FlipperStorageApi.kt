package com.flipperdevices.bridge.rpc.api

import com.flipperdevices.bridge.rpc.api.model.NameWithHash
import com.flipperdevices.core.progress.ProgressListener
import java.io.File

interface FlipperStorageApi {
    suspend fun mkdirs(path: String)

    suspend fun delete(path: String, recursive: Boolean = false)

    suspend fun download(
        pathOnFlipper: String,
        fileOnAndroid: File,
        progressListener: ProgressListener
    )

    suspend fun upload(
        pathOnFlipper: String,
        fileOnAndroid: File,
        progressListener: ProgressListener
    )

    suspend fun listingDirectory(pathOnFlipper: String): List<String>

    suspend fun listingDirectoryWithMd5(pathOnFlipper: String): List<NameWithHash>
}
