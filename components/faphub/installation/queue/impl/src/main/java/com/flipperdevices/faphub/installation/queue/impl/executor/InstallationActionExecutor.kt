package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.utils.FapHubConstants.FLIPPER_APPS_FOLDER
import java.io.File
import javax.inject.Inject

private const val PERCENT_FOR_DOWNLOAD = 0.1f
private const val PERCENT_FOR_UPLOAD = 0.99f

class InstallationActionExecutor @Inject constructor(
    private val fapDownloadApi: FapDownloadApi,
    private val fapUploadAction: FapActionUpload,
    private val fapManifestApi: FapManifestApi
) : LogTagProvider {
    override val TAG = "InstallationActionExecutor"

    suspend fun install(
        request: FapActionRequest.Install,
        progressListener: ProgressListener
    ) {
        info { "Start install $request" }
        val downloadedFap = fapDownloadApi.downloadBundle(
            versionId = request.toVersionId,
            listener = ProgressWrapperTracker(
                progressListener,
                max = PERCENT_FOR_DOWNLOAD
            )
        )
        info { "Fap downloaded by request $request" }
        val path = fapUploadAction.upload(
            downloadedFap,
            ProgressWrapperTracker(
                progressListener,
                min = PERCENT_FOR_DOWNLOAD,
                max = PERCENT_FOR_UPLOAD
            )
        )
        info { "Fap uploaded by request $request" }
        var finalFapPath = File(FLIPPER_APPS_FOLDER, request.categoryAlias).absolutePath
        finalFapPath = File(finalFapPath, "${request.applicationAlias}.fap").absolutePath
        fapManifestApi.add(
            pathToFap = path,
            FapManifestItem(
                applicationAlias = request.applicationAlias,
                uid = request.applicationUid,
                versionUid = request.toVersionId,
                path = finalFapPath
            )
        )
        info { "Fap manifest added by request $request" }
    }
}
