package com.flipperdevices.updater.impl

import android.content.Context
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.update.api.FUpdateFeatureApi
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.FOneTimeExecutionBleTask
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
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.net.UnknownHostException
import javax.inject.Inject

private const val DISCONNECT_WAIT_TIMEOUT_MS = 30 * 1000L

@Suppress("LongParameterList")
class UpdaterTask @Inject constructor(
    private val uploadToFlipperHelper: UploadToFlipperHelper,
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper,
    private val updateContentDownloader: MutableSet<UpdateContentDownloader>,
    private val fapNeedUpdatePopUpHelper: FapNeedUpdatePopUpHelper,
    private val storageProvider: FlipperStorageProvider,
    private val flipperUpdateImageHelper: FlipperUpdateImageHelper,
    private val orchestrator: FDeviceOrchestrator,
    private val fFeatureProvider: FFeatureProvider
) : FOneTimeExecutionBleTask<UpdateRequest, UpdatingState>(),
    LogTagProvider {
    override val TAG = "UpdaterTask"


    private var isStoppedManually = false

    override suspend fun onStopAsync(stateListener: suspend (UpdatingState) -> Unit) {
        if (!isStoppedManually) {
            stateListener(UpdatingState.NotStarted)
        }
    }

    override suspend fun startInternal(
        scope: CoroutineScope,
        input: UpdateRequest,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        val fUpdateFeatureApi: FUpdateFeatureApi = fFeatureProvider.getSync() ?: run {
            error { "#startInternal could not get FUpdateFeatureApi" }
            return
        }
        val fGetInfoFeatureApi: FGetInfoFeatureApi = fFeatureProvider.getSync() ?: run {
            error { "#startInternal could not get FGetInfoFeatureApi" }
            return
        }
        val fFileUploadApi = fFeatureProvider.getSync<FStorageFeatureApi>()?.uploadApi() ?: run {
            error { "#startInternal could not get FFileUploadApi" }
            return
        }
        val fListingStorageApi: FListingStorageApi = fFeatureProvider.getSync<FStorageFeatureApi>()?.listingApi() ?: run {
            error { "#startInternal could not get FListingStorageApi" }
            return
        }
        try {
            startInternalUnwrapped(
                input = input,
                fFileUploadApi = fFileUploadApi,
                fGetInfoFeatureApi = fGetInfoFeatureApi,
                fListingStorageApi = fListingStorageApi,
                fUpdateFeatureApi = fUpdateFeatureApi,
                stateListener = {
                    if (it.isFinalState) {
                        isStoppedManually = true
                    }

                    stateListener(it)
                }
            )
        } finally {
            withContext(NonCancellable) {
                flipperUpdateImageHelper.stopImageOnFlipperSafe(fUpdateFeatureApi)
            }
        }
    }

    @Suppress("LongMethod", "ComplexMethod")
    private suspend fun startInternalUnwrapped(
        input: UpdateRequest,
        fGetInfoFeatureApi: FGetInfoFeatureApi,
        fFileUploadApi: FFileUploadApi,
        fUpdateFeatureApi: FUpdateFeatureApi,
        fListingStorageApi: FListingStorageApi,
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
            subGhzProvisioningHelper.provideAndUploadSubGhz(
                fGetInfoFeatureApi = fGetInfoFeatureApi,
                fFileUploadApi = fFileUploadApi
            )
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
            prepareToUpload(updaterFolder, fFileUploadApi, fUpdateFeatureApi)
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
                flipperPath = flipperPath,
                updaterFolder = updaterFolder,
                fUpdateFeatureApi = fUpdateFeatureApi,
                fListingStorageApi = fListingStorageApi,
                fFileUploadApi = fFileUploadApi,
                stateListener = stateListener
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
            orchestrator.getState()
                .filterIsInstance<FDeviceConnectStatus.Disconnected>()
                .first()
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
        fFileUploadApi: FFileUploadApi,
        fUpdateFeatureApi: FUpdateFeatureApi
    ): String {
        flipperUpdateImageHelper.loadImageOnFlipper(fUpdateFeatureApi)

        val updateName: String = updaterFolder
            .listFiles()
            ?.first { it.isDirectory }
            ?.name
            ?: updaterFolder.name

        val flipperPath = "/ext/update/$updateName"
        fFileUploadApi.mkdir(flipperPath)
            .onFailure { error(it) { "#prepareToUpload could not mkdir" } }
        return flipperPath
    }
}
