package com.flipperdevices.faphub.installation.queue.impl.executor

import androidx.datastore.core.DataStore
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.faphub.dao.api.FapDownloadApi
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapActionUpload
import com.flipperdevices.faphub.installation.queue.impl.executor.actions.FapIconDownloader
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.flipperdevices.faphub.utils.FapHubConstants.FLIPPER_APPS_FOLDER
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

class InstallationActionExecutor @Inject constructor(
    fapDownloadApi: FapDownloadApi,
    fapUploadAction: FapActionUpload,
    fapNetworkApi: FapNetworkApi,
    private val dataStoreSettings: DataStore<Settings>,
    private val fapManifestApi: FapManifestApi,
    private val fapIconDownloader: FapIconDownloader
) : PrepareFapActionExecutor(fapDownloadApi, fapUploadAction, fapNetworkApi), LogTagProvider {
    override val TAG = "InstallationActionExecutor"

    suspend fun install(
        request: FapActionRequest.Install,
        progressListener: ProgressListener
    ) {
        val target = request.toVersion.target as? FlipperTarget.Received
            ?: error("Failed download fap for $request")
        val path = uploadAndDownloadFap(request.toVersion.id, target, progressListener)
        var finalFapPath = File(FLIPPER_APPS_FOLDER, request.categoryAlias).absolutePath
        finalFapPath = File(finalFapPath, "${request.applicationAlias}.fap").absolutePath

        val iconBase64Request = fapIconDownloader.downloadToBase64(request.iconUrl).onFailure {
            error(it) { "Failed download ${request.iconUrl}" }
        }

        fapManifestApi.add(
            pathToFap = path,
            fapManifestItem = FapManifestItem(
                applicationAlias = request.applicationAlias,
                uid = request.applicationUid,
                versionUid = request.toVersion.id,
                path = finalFapPath,
                fullName = request.applicationName,
                iconBase64 = iconBase64Request.getOrNull(),
                sdkApi = getSdkApi(request.applicationUid, request.toVersion),
                isDevCatalog = dataStoreSettings.data.first().use_dev_catalog
            )
        )
        info { "Fap manifest added by request $request" }
    }
}
