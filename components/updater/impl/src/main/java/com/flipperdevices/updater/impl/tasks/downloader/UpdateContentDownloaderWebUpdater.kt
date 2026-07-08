package com.flipperdevices.updater.impl.tasks.downloader

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.UpdateContent
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.WebUpdaterFirmware
import dev.zacsweers.metro.ContributesIntoSet
import java.io.File
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoSet(AppGraph::class, binding<UpdateContentDownloader>())
class UpdateContentDownloaderWebUpdater @Inject constructor(
    private val firmwareDownloaderHelper: FirmwareDownloaderHelper
) : UpdateContentDownloader {
    override fun isSupport(updateContent: UpdateContent): Boolean {
        return updateContent is WebUpdaterFirmware
    }

    override suspend fun downloadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        require(updateContent is WebUpdaterFirmware) { "Content not compare" }
        stateListener(
            UpdatingState.DownloadingFromNetwork(0f)
        )
        firmwareDownloaderHelper.downloadFirmware(
            updateFile = DistributionFile(updateContent.url),
            updaterFolder = updaterFolder,
            stateListener = stateListener
        )
    }
}
