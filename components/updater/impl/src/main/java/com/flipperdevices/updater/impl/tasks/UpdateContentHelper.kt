package com.flipperdevices.updater.impl.tasks

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.DownloadAndUnpackDelegate
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.InternalStorageFirmware
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.UpdateContent
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.WebUpdaterFirmware
import com.squareup.anvil.annotations.ContributesMultibinding
import java.io.File
import javax.inject.Inject

interface UpdateContentHelper {
    fun isSupport(updateContent: UpdateContent): Boolean
    suspend fun uploadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    )
}

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateContentHelper::class)
class UpdateContentHelperOfficial @Inject constructor(
    private val firmwareDownloaderHelper: FirmwareDownloaderHelper
) : UpdateContentHelper {
    override fun isSupport(updateContent: UpdateContent): Boolean {
        return updateContent is OfficialFirmware
    }

    override suspend fun uploadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        if (updateContent !is OfficialFirmware) {
            throw IllegalArgumentException("Content not compare")
        }
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

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateContentHelper::class)
class UpdateContentHelperInternalStorage @Inject constructor(
    private val downloadAndUnpackDelegate: DownloadAndUnpackDelegate
) : UpdateContentHelper {
    override fun isSupport(updateContent: UpdateContent): Boolean {
        return updateContent is InternalStorageFirmware
    }

    override suspend fun uploadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        if (updateContent !is InternalStorageFirmware) {
            throw IllegalArgumentException("Content not compare")
        }
        val internalFile = updateContent.file
        downloadAndUnpackDelegate.unpack(internalFile, updaterFolder)
    }
}

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateContentHelper::class)
class UpdateContentHelperWepUpdater @Inject constructor(
    private val firmwareDownloaderHelper: FirmwareDownloaderHelper
) : UpdateContentHelper {
    override fun isSupport(updateContent: UpdateContent): Boolean {
        return updateContent is WebUpdaterFirmware
    }

    override suspend fun uploadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        if (updateContent !is WebUpdaterFirmware) {
            throw IllegalArgumentException("Content not compare")
        }
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
