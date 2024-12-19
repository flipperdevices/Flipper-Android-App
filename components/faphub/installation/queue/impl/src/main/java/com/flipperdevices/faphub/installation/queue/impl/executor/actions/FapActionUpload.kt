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
import kotlinx.coroutines.runBlocking
import okio.buffer
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
        fStorageFeatureApi.uploadApi()
            .mkdir(FapHubConstants.FLIPPER_TMP_FOLDER_PATH)
            .onFailure { error(it) { "#upload could not create dir ${FapHubConstants.FLIPPER_TMP_FOLDER_PATH}" } }
        val fapPath = File(
            FapHubConstants.FLIPPER_TMP_FOLDER_PATH,
            "tmp.fap"
        ).absolutePath
        val progressWrapper = ProgressWrapperTracker(progressListener)
        runCatching {
            fapFile.source().buffer().use { source ->
                fStorageFeatureApi.uploadApi().sink(fapPath).use { sink ->
                    source.copyWithProgress(
                        sink = sink,
                        sourceLength = { fapFile.length() },
                        progressListener = { current, max ->
                            runBlocking { progressWrapper.onProgress(current, max) }
                        }
                    )
                }
            }
        }.onFailure { error(it) { "Failed upload tmp manifest" } }.getOrThrow()

        return fapPath
    }
}
