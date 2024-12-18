package com.flipperdevices.faphub.installation.queue.impl.executor.actions

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.faphub.utils.FapHubConstants
import okio.source
import java.io.File
import javax.inject.Inject

class FapActionUpload @Inject constructor(
    private val fFeatureProvider: FFeatureProvider,
) : LogTagProvider {
    override val TAG = "FapActionUpload"

    suspend fun upload(
        fapFile: File,
        progressListener: ProgressListener
    ): String {
        info { "#upload Start upload ${fapFile.absolutePath}" }
        val fStorageFeatureApi = fFeatureProvider
            .getSync<FStorageFeatureApi>()
            ?: run {
                error { "#upload could not get FStorageFeatureApi" }
                error("Could not get FStorageFeatureApi")
            }
        info { "#upload got upload feature!" }
        fStorageFeatureApi.uploadApi().mkdir(FapHubConstants.FLIPPER_TMP_FOLDER_PATH)
        val fapPath = File(
            FapHubConstants.FLIPPER_TMP_FOLDER_PATH,
            "tmp.fap"
        ).absolutePath
        info { "#upload File is: $fapPath" }
        val progressWrapper = ProgressWrapperTracker(progressListener)
        runCatching {
            info { "#upload fapFile.inputStream()" }
            fapFile.inputStream().use { inputStream ->
                info { "#upload inputStream.source()" }
                inputStream.source().copyWithProgress(
                    sink = fStorageFeatureApi.uploadApi().sink(fapPath),
                    progressListener = { current, max ->
                        info { "#upload onProgress: $current $max" }
                        progressWrapper.onProgress(current, max)
                        info { "#upload onProgress invoked" }
                    }
                )
                info { "#upload inputStream.source() finished!" }
            }
            info { "#upload fapFile.inputStream() finished!" }
        }.onFailure { error(it) { "Failed upload tmp manifest" } }.getOrThrow()

        return fapPath
    }
}
