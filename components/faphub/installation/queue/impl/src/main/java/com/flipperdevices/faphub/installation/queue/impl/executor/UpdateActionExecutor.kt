package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapIconDownloader
import com.flipperdevices.faphub.target.model.FlipperTarget
import javax.inject.Inject

class UpdateActionExecutor @Inject constructor(
    fapDownloadApi: FapDownloadApi,
    fapUploadAction: FapActionUpload,
    fapNetworkApi: FapNetworkApi,
    private val fapManifestApi: FapManifestApi,
    private val fapIconDownloader: FapIconDownloader
) : PrepareFapActionExecutor(fapDownloadApi, fapUploadAction, fapNetworkApi) {
    override val TAG = "UpdateActionExecutor"

    suspend fun update(
        request: FapActionRequest.Update,
        progressListener: ProgressListener
    ) {
        val target = request.toVersion.target as? FlipperTarget.Received
            ?: error("Failed download fap for $request")
        val path = uploadAndDownloadFap(request.toVersion.id, target, progressListener)

        val iconBase64Request = fapIconDownloader.downloadToBase64(request.iconUrl).onFailure {
            error(it) { "Failed download ${request.iconUrl}" }
        }

        fapManifestApi.add(
            pathToFap = path,
            fapManifestItem = request.from.copy(
                fullName = request.applicationName,
                versionUid = request.toVersion.id,
                iconBase64 = iconBase64Request.getOrNull() ?: request.from.iconBase64,
                sdkApi = getSdkApi(request.applicationUid, request.toVersion)
            )
        )
        info { "Fap manifest added by request $request" }
    }
}
