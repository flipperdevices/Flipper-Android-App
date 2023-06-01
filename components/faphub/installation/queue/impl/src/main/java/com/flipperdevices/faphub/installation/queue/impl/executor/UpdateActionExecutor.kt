package com.flipperdevices.faphub.installation.queue.impl.executor

import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestVersion
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import javax.inject.Inject

class UpdateActionExecutor @Inject constructor(
    fapDownloadApi: FapDownloadApi,
    fapUploadAction: FapActionUpload,
    private val fapManifestApi: FapManifestApi
) : PrepareFapActionExecutor(fapDownloadApi, fapUploadAction) {
    override val TAG = "UpdateActionExecutor"

    suspend fun update(
        request: FapActionRequest.Update,
        progressListener: ProgressListener
    ) {
        val path = uploadAndDownloadFap(request.toVersion.id, progressListener)
        fapManifestApi.add(
            pathToFap = path,
            request.from.copy(
                version = FapManifestVersion(
                    versionUid = request.toVersion.id,
                    semVer = request.toVersion.version
                )
            )
        )
        info { "Fap manifest added by request $request" }
    }
}
