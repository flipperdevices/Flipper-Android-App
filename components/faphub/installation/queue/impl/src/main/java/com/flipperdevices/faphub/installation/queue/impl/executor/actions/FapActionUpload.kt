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
import com.flipperdevices.faphub.utils.FapHubTmpFolderProvider
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okio.source
import java.io.File
import javax.inject.Inject

class FapActionUpload @Inject constructor(
    private val fFeatureProvider: FFeatureProvider,
    private val tmpFolderProvider: FapHubTmpFolderProvider
) : LogTagProvider {
    override val TAG = "FapActionUpload"

    suspend fun upload(
        fapFile: File,
        progressListener: ProgressListener
    ): String {
        info { "Start upload ${fapFile.absolutePath}" }
        val fStorageFeatureApi = fFeatureProvider
            .getSync<FStorageFeatureApi>()
            ?: error("Could not get FStorageFeatureApi")
        val fapPath = File(
            tmpFolderProvider.provideTmpFolder(),
            "tmp.fap"
        ).absolutePath
        val progressWrapper = ProgressWrapperTracker(progressListener)
        runCatching {
            fapFile.inputStream().use { inputStream ->
                inputStream.source().copyWithProgress(
                    sink = fStorageFeatureApi.uploadApi().sink(fapPath),
                    progressListener = { current, max ->
                        progressWrapper.onProgress(current, max)
                    }
                )
            }
        }.onFailure { error(it) { "Failed upload tmp manifest" } }.getOrThrow()

        return fapPath
    }
}
