package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestVersion
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapIconDownloader
import com.flipperdevices.faphub.utils.FapHubConstants.FLIPPER_APPS_FOLDER
import java.io.File
import javax.inject.Inject

class InstallationActionExecutor @Inject constructor(
    fapDownloadApi: FapDownloadApi,
    fapUploadAction: FapActionUpload,
    private val fapManifestApi: FapManifestApi,
    private val fapIconDownloader: FapIconDownloader
) : PrepareFapActionExecutor(fapDownloadApi, fapUploadAction), LogTagProvider {
    override val TAG = "InstallationActionExecutor"

    suspend fun install(
        request: FapActionRequest.Install,
        progressListener: ProgressListener
    ) {
        val path = uploadAndDownloadFap(request.toVersion, progressListener)
        var finalFapPath = File(FLIPPER_APPS_FOLDER, request.categoryAlias).absolutePath
        finalFapPath = File(finalFapPath, "${request.applicationAlias}.fap").absolutePath

        val iconBase64Request = fapIconDownloader.downloadToBase64(request.iconUrl).onFailure {
            error(it) { "Failed download ${request.iconUrl}" }
        }

        fapManifestApi.add(
            pathToFap = path,
            FapManifestItem(
                applicationAlias = request.applicationAlias,
                uid = request.applicationUid,
                version = FapManifestVersion(
                    versionUid = request.toVersion.id,
                    semVer = request.toVersion.version
                ),
                path = finalFapPath,
                fullName = request.applicationName,
                iconBase64 = iconBase64Request.getOrNull()
            )
        )
        info { "Fap manifest added by request $request" }
    }
}
