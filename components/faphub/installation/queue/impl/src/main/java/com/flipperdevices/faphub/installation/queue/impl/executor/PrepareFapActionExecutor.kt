package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.target.model.FlipperTarget

private const val PERCENT_FOR_DOWNLOAD = 0.1f
private const val PERCENT_FOR_UPLOAD = 0.99f

abstract class PrepareFapActionExecutor(
    private val fapDownloadApi: FapDownloadApi,
    private val fapUploadAction: FapActionUpload
) : LogTagProvider {
    protected suspend fun uploadAndDownloadFap(
        version: FapItemVersion,
        progressListener: ProgressListener
    ): String {
        info { "Start download $version" }
        val target = version.target as? FlipperTarget.Received
            ?: error("Failed download fap for $version")
        val downloadedFap = fapDownloadApi.downloadBundle(
            versionId = version.id,
            listener = ProgressWrapperTracker(
                progressListener,
                max = PERCENT_FOR_DOWNLOAD
            ),
            target = target
        )
        info { "Fap downloaded by request $version to ${downloadedFap.path}" }
        val path = fapUploadAction.upload(
            downloadedFap,
            ProgressWrapperTracker(
                progressListener,
                min = PERCENT_FOR_DOWNLOAD,
                max = PERCENT_FOR_UPLOAD
            )
        )
        info { "Fap uploaded by request $version to $path" }
        return path
    }
}
