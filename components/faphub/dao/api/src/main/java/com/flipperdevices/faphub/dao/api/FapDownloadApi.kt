package com.flipperdevices.faphub.dao.api

import com.flipperdevices.core.progress.ProgressListener
import java.io.File

interface FapDownloadApi {
    suspend fun downloadBundle(
        versionId: String,
        listener: ProgressListener? = null
    ): File
}
