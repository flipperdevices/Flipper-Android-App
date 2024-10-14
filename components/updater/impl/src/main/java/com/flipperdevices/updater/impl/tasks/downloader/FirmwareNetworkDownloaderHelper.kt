package com.flipperdevices.updater.impl.tasks.downloader

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject

interface FirmwareDownloaderHelper {
    suspend fun downloadFirmware(
        updateFile: DistributionFile,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    )
}

@ContributesBinding(AppGraph::class, FirmwareDownloaderHelper::class)
class FirmwareDownloaderHelperImpl @Inject constructor(
    private val downloaderApi: DownloaderApi
) : FirmwareDownloaderHelper, LogTagProvider {
    override val TAG = "FirmwareDownloaderHelper"

    override suspend fun downloadFirmware(
        updateFile: DistributionFile,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        stateListener(UpdatingState.DownloadingFromNetwork(percent = 0.0001f))
        downloaderApi.download(updateFile, updaterFolder, decompress = true).collect {
            when (it) {
                DownloadProgress.Finished ->
                    stateListener(UpdatingState.DownloadingFromNetwork(1.0f))

                is DownloadProgress.InProgress -> {
                    val totalBytes = it.totalBytes
                    val progress = if (totalBytes == null) {
                        0f
                    } else {
                        it.processedBytes.toFloat() / totalBytes.toFloat()
                    }
                    stateListener(UpdatingState.DownloadingFromNetwork(progress))
                }

                DownloadProgress.NotStarted -> stateListener(UpdatingState.NotStarted)
            }
        }
    }
}
