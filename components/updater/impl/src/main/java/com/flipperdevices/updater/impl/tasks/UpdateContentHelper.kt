package com.flipperdevices.updater.impl.tasks

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.updater.api.DownloadAndUnpackDelegateApi
import com.flipperdevices.updater.impl.model.UpdateContentException
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

private const val MANIFEST_NAME = "update.fuf"

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

@ContributesMultibinding(scope = AppGraph::class, boundType = UpdateContentHelper::class)
class UpdateContentHelperInternalStorage @Inject constructor(
    private val downloadAndUnpackDelegateApi: DownloadAndUnpackDelegateApi,
    private val deeplinkParser: DeepLinkParser,
    private val context: Context
) : UpdateContentHelper {
    override fun isSupport(updateContent: UpdateContent): Boolean {
        return updateContent is InternalStorageFirmware
    }

    override suspend fun uploadFirmwareLocal(
        updateContent: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        require(updateContent is InternalStorageFirmware) { "Content not compare" }
        val deeplink = deeplinkParser.fromUri(context, updateContent.uri)
        val deeplinkContent = if (deeplink is Deeplink.FlipperKey) {
            deeplink.content ?: throw UpdateContentException()
        } else throw UpdateContentException()

        if (deeplinkContent is DeeplinkContent.InternalStorageFile) {
            kotlin.runCatching {
                downloadAndUnpackDelegateApi.unpack(deeplinkContent.file, updaterFolder)
            }.onFailure { throw UpdateContentException() }
        } else throw UpdateContentException()

        if (isManifestNotExist(updaterFolder)) throw UpdateContentException()
    }

    private fun isManifestNotExist(updaterFolder: File): Boolean {
        val files = updaterFolder.listFiles()?.firstOrNull()?.listFiles()
        val manifest = files?.filter { it.name == MANIFEST_NAME }
        return manifest?.isEmpty() ?: true
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
