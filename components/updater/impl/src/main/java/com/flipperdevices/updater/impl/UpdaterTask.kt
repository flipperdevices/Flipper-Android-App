package com.flipperdevices.updater.impl

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.faphub.installedtab.api.FapNeedUpdatePopUpHelper
import com.flipperdevices.updater.impl.model.IntFlashFullException
import com.flipperdevices.updater.impl.model.UpdateContentException
import com.flipperdevices.updater.impl.tasks.FlipperUpdateImageHelper
import com.flipperdevices.updater.impl.tasks.UploadToFlipperHelper
import com.flipperdevices.updater.impl.tasks.downloader.UpdateContentDownloader
import com.flipperdevices.updater.model.OfficialFirmware
import com.flipperdevices.updater.model.SubGhzProvisioningException
import com.flipperdevices.updater.model.UpdateContent
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.flipperdevices.updater.subghz.model.FailedUploadSubGhzException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.net.UnknownHostException

private const val DISCONNECT_WAIT_TIMEOUT_MS = 30 * 1000L

@Suppress("LongParameterList")
class UpdaterTask(
    serviceProvider: FlipperServiceProvider,
    private val context: Context,
    private val uploadToFlipperHelper: UploadToFlipperHelper,
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper,
    private val updateContentDownloader: MutableSet<UpdateContentDownloader>,
    private val flipperStorageApi: FlipperStorageApi,
    private val fapNeedUpdatePopUpHelper: FapNeedUpdatePopUpHelper,
    private val storageProvider: FlipperStorageProvider
) : OneTimeExecutionBleTask<UpdateRequest, UpdatingState>(serviceProvider),
    LogTagProvider {
    override val TAG = "UpdaterTask"

    private val flipperUpdateImageHelper = FlipperUpdateImageHelper(context)

    private var isStoppedManually = false

    override suspend fun onStopAsync(stateListener: suspend (UpdatingState) -> Unit) {
        if (!isStoppedManually) {
            stateListener(UpdatingState.NotStarted)
        }
    }

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: UpdateRequest,
        stateListener: suspend (UpdatingState) -> Unit
    ) = try {
        startInternalUnwrapped(serviceApi, input) {
            if (it.isFinalState) {
                isStoppedManually = true
            }

            stateListener(it)
        }
    } finally {
        withContext(NonCancellable) {
            flipperUpdateImageHelper.stopImageOnFlipperSafe(serviceApi.requestApi)
        }
    }

    @Suppress("LongMethod", "ComplexMethod")
    private suspend fun startInternalUnwrapped(
        serviceApi: FlipperServiceApi,
        input: UpdateRequest,
        stateListener: suspend (UpdatingState) -> Unit
    ) = storageProvider.useTemporaryFolder { tempFolder ->
        info { "Start update with folder: $tempFolder" }
        val updateContent = input.content

        val updaterFolder = File(tempFolder.toFile(), updateContent.folderName())
        try {
            downloadFirmwareLocal(input.content, updaterFolder, stateListener)
        } catch (e: Throwable) {
            error(e) { "Failed when download from network" }
            when (e) {
                is UpdateContentException -> stateListener(UpdatingState.FailedCustomUpdate)
                is CancellationException -> {}
                else -> if (input.content is OfficialFirmware) {
                    stateListener(UpdatingState.FailedDownload)
                } else {
                    stateListener(UpdatingState.FailedCustomUpdate)
                }
            }
            return@useTemporaryFolder
        }
        try {
            stateListener(UpdatingState.SubGhzProvisioning)
            subGhzProvisioningHelper.provideAndUploadSubGhz(serviceApi)
        } catch (e: SubGhzProvisioningException) {
            error(e) { "Failed receive subghz region" }
            stateListener(UpdatingState.FailedOutdatedApp)
            return@useTemporaryFolder
        } catch (e: FailedUploadSubGhzException) {
            error(e) { "Failed upload subghz provisioning" }
            stateListener(UpdatingState.FailedSubGhzProvisioning)
            return@useTemporaryFolder
        } catch (e: UnknownHostException) {
            error(e) { "Failed download subghz information" }
            stateListener(UpdatingState.FailedDownload)
            return@useTemporaryFolder
        } catch (e: CancellationException) {
            error(e) { "Cancel update" }
            return@useTemporaryFolder
        } catch (e: Throwable) {
            error(e) { "Failed when provide subghz provisioning" }
            stateListener(UpdatingState.FailedPrepare)
            return@useTemporaryFolder
        }

        stateListener(
            UpdatingState.UploadOnFlipper(0f)
        )
        val flipperPath = try {
            prepareToUpload(updaterFolder, serviceApi.requestApi)
        } catch (e: CancellationException) {
            error(e) { "Cancel prepare to upload" }
            return@useTemporaryFolder
        } catch (e: Throwable) {
            error(e) { "Failed when prepare upload to flipper" }
            stateListener(UpdatingState.FailedPrepare)
            return@useTemporaryFolder
        }

        try {
            uploadToFlipperHelper.uploadToFlipper(
                flipperPath,
                updaterFolder,
                serviceApi.requestApi,
                stateListener
            )
        } catch (e: Throwable) {
            error(e) { "Failed when upload to flipper" }
            when (e) {
                is IntFlashFullException -> stateListener(UpdatingState.FailedInternalStorage)
                is CancellationException -> {}
                else -> stateListener(UpdatingState.FailedUpload)
            }
            return@useTemporaryFolder
        }

        withTimeoutOrNull(DISCONNECT_WAIT_TIMEOUT_MS) {
            serviceApi.connectionInformationApi.getConnectionStateFlow()
                .filter { !it.isConnected }.first()
        }

        fapNeedUpdatePopUpHelper.notifyIfUpdateAvailable()

        stateListener(UpdatingState.Rebooting)
    }

    private suspend fun downloadFirmwareLocal(
        content: UpdateContent,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        val downloadHelper = updateContentDownloader
            .firstOrNull { it.isSupport(content) }
            ?: throw IllegalArgumentException("No one helper for upload fw to local")
        downloadHelper.downloadFirmwareLocal(content, updaterFolder, stateListener)
    }

    private suspend fun prepareToUpload(
        updaterFolder: File,
        requestApi: FlipperRequestApi
    ): String {
        flipperUpdateImageHelper.loadImageOnFlipper(requestApi)

        val updateName: String = updaterFolder
            .listFiles()
            ?.first { it.isDirectory }
            ?.name
            ?: updaterFolder.name

        val flipperPath = "/ext/update/$updateName"

        flipperStorageApi.mkdirs(flipperPath)
        return flipperPath
    }
}
