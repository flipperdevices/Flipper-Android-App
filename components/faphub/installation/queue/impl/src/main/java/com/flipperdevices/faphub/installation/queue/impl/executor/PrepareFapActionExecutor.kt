package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload

private const val PERCENT_FOR_DOWNLOAD = 0.1f
private const val PERCENT_FOR_UPLOAD = 0.99f

abstract class PrepareFapActionExecutor(
    private val fapDownloadApi: FapDownloadApi,
    private val fapUploadAction: FapActionUpload
) : LogTagProvider {
    protected suspend fun uploadAndDownloadFap(
        versionId: String,
        progressListener: ProgressListener
    ): String {
        info { "Start download $versionId" }
        val downloadedFap = fapDownloadApi.downloadBundle(
            versionId = versionId,
            listener = ProgressWrapperTracker(
                progressListener,
                max = PERCENT_FOR_DOWNLOAD
            )
        )
        info { "Fap downloaded by request $versionId to ${downloadedFap.path}" }
        val path = fapUploadAction.upload(
            downloadedFap,
            ProgressWrapperTracker(
                progressListener,
                min = PERCENT_FOR_DOWNLOAD,
                max = PERCENT_FOR_UPLOAD
            )
        )
        info { "Fap uploaded by request $versionId to $path" }
        return path
    }
}
