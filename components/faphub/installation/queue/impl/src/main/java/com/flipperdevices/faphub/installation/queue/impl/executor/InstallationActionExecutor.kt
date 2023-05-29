package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.faphub.constants.FapHubConstants.FLIPPER_APPS_FOLDER
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import java.io.File
import javax.inject.Inject

private const val PERCENT_FOR_DOWNLOAD = 0.1f

class InstallationActionExecutor @Inject constructor(
    private val fapDownloadApi: FapDownloadApi,
    private val fapUploadAction: FapActionUpload,
    private val fapManifestApi: FapManifestApi
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
        val path = fapUploadAction.upload(
            downloadedFap,
            ProgressWrapperTracker(
                progressListener,
                min = PERCENT_FOR_DOWNLOAD
            )
        )
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
    }
}