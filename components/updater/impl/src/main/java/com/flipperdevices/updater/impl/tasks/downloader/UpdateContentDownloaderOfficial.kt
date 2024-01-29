package com.flipperdevices.updater.impl.tasks.downloader

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateContent
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesMultibinding
import java.io.File
import javax.inject.Inject

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateContentDownloader::class)
class UpdateContentDownloaderOfficial @Inject constructor(
    private val firmwareDownloaderHelper: FirmwareDownloaderHelper
) : UpdateContentDownloader {
    override fun isSupport(updateContent: UpdateContent): Boolean {
        return updateContent is OfficialFirmware
    }

    override suspend fun downloadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        require(updateContent is OfficialFirmware) { "Content not compare" }
        stateListener(
            UpdatingState.DownloadingFromNetwork(0f)
        )
        firmwareDownloaderHelper.downloadFirmware(
            updateFile = updateContent.distributionFile,
            updaterFolder = updaterFolder,
            stateListener = stateListener
        )
    }
}
