package com.flipperdevices.faphub.dao.api

import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.target.model.FlipperTarget
import java.io.File

interface FapDownloadApi {
    suspend fun downloadBundle(
        target: FlipperTarget.Received,
        versionUid: String,
        listener: ProgressListener? = null
    ): File
}
