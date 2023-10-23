package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.target.model.FlipperTarget

private const val PERCENT_FOR_DOWNLOAD = 0.1f
private const val PERCENT_FOR_UPLOAD = 0.99f

abstract class PrepareFapActionExecutor(
    private val fapDownloadApi: FapDownloadApi,
    private val fapUploadAction: FapActionUpload,
    private val fapNetworkApi: FapNetworkApi
) : LogTagProvider {
    protected suspend fun uploadAndDownloadFap(
        versionUid: String,
        target: FlipperTarget.Received,
        progressListener: ProgressListener
    ): String {
        info { "Start download $versionUid" }
        val downloadedFap = fapDownloadApi.downloadBundle(
            versionUid = versionUid,
            listener = ProgressWrapperTracker(
                progressListener,
                max = PERCENT_FOR_DOWNLOAD
            ),
            target = target
        )
        info { "Fap downloaded by request $versionUid to ${downloadedFap.path}" }
        val path = fapUploadAction.upload(
            downloadedFap,
            ProgressWrapperTracker(
                progressListener,
                min = PERCENT_FOR_DOWNLOAD,
                max = PERCENT_FOR_UPLOAD
            )
        )
        info { "Fap uploaded by request $versionUid to $path" }
        return path
    }

    protected suspend fun getSdkApi(appId: String, fapVersion: FapItemVersion): SemVer? {
        if (fapVersion.sdkApi != null) {
            return fapVersion.sdkApi
        }
        val fapItem = fapNetworkApi.getFapItemById(fapVersion.target, appId)
            .getOrNull()

        if (fapItem?.upToDateVersion?.sdkApi != null) {
            return fapItem.upToDateVersion.sdkApi
        }

        return (fapVersion.target as? FlipperTarget.Received)?.sdk
    }
}
