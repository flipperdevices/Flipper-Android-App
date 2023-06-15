package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestVersion
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapIconDownloader
import com.flipperdevices.faphub.target.model.FlipperTarget
import javax.inject.Inject

class UpdateActionExecutor @Inject constructor(
    fapDownloadApi: FapDownloadApi,
    fapUploadAction: FapActionUpload,
    private val fapManifestApi: FapManifestApi,
    private val fapIconDownloader: FapIconDownloader
) : PrepareFapActionExecutor(fapDownloadApi, fapUploadAction) {
    override val TAG = "UpdateActionExecutor"

    suspend fun update(
        request: FapActionRequest.Update,
        progressListener: ProgressListener
    ) {
        val path = uploadAndDownloadFap(request.toVersion, progressListener)

        val iconBase64Request = fapIconDownloader.downloadToBase64(request.iconUrl).onFailure {
            error(it) { "Failed download ${request.iconUrl}" }
        }

        fapManifestApi.add(
            pathToFap = path,
            request.from.copy(
                version = FapManifestVersion(
                    versionUid = request.toVersion.id,
                    semVer = request.toVersion.version,
                ),
                iconBase64 = iconBase64Request.getOrNull() ?: request.from.iconBase64,
                sdkApi = (request.toVersion.target as? FlipperTarget.Received)?.sdk
            )
        )
        info { "Fap manifest added by request $request" }
    }
}
