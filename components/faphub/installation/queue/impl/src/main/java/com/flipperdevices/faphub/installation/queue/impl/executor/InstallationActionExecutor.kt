package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.queue.impl.model.FapActionRequest
import javax.inject.Inject

private const val PERCENT_FOR_DOWNLOAD = 0.1f

class InstallationActionExecutor @Inject constructor(
    private val fapDownloadApi: FapDownloadApi
) {
    suspend fun install(
        request: FapActionRequest.Install,
        progressListener: ProgressListener
    ) {
        val downloadedFap = fapDownloadApi.downloadBundle(
            request.toVersionId,
            ProgressWrapperTracker(
                progressListener,
                max = PERCENT_FOR_DOWNLOAD
            )
        )
    }
}