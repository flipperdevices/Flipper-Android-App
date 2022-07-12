package com.flipperdevices.updater.impl

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.System
import com.flipperdevices.protobuf.system.rebootRequest
import com.flipperdevices.protobuf.system.updateRequest
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.impl.service.UploadFirmwareService
import com.flipperdevices.updater.impl.tasks.FlipperUpdateImageHelper
import com.flipperdevices.updater.impl.utils.FolderCreateHelper
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingState
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdaterTask(
    serviceProvider: FlipperServiceProvider,
    private val downloaderApi: DownloaderApi,
    private val context: Context
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
    ) {
        startInternalUnwrapped(scope, serviceApi, input) {
            if (it.isFinalState) {
                isStoppedManually = true
                withContext(NonCancellable) {
                    flipperUpdateImageHelper.stopImageOnFlipperSafe(serviceApi.requestApi)
                }
            }
            stateListener(it)
        }
    }

    private suspend fun startInternalUnwrapped(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: UpdateRequest,
        stateListener: suspend (UpdatingState) -> Unit
    ) = FlipperStorageProvider.useTemporaryFolder(context) { tempFolder ->
        info { "Start update with folder: ${tempFolder.absolutePath}" }
        val updateFile = input.updateTo.updaterFile

        val updaterFolder = File(tempFolder, updateFile.sha256)
        try {
            stateListener(
                UpdatingState.DownloadingFromNetwork(0f)
            )
            downloadFirmware(updateFile, updaterFolder, stateListener)
        } catch (e: Throwable) {
            error(e) { "Failed when download from network" }
            if (e !is CancellationException) {
                stateListener(UpdatingState.FailedDownload)
            }
            return@useTemporaryFolder
        }

        stateListener(
            UpdatingState.UploadOnFlipper(0f)
        )
        val flipperPath = try {
            prepareToUpload(updateFile, serviceApi.requestApi)
        } catch (e: Throwable) {
            error(e) { "Failed when prepare upload to flipper" }
            if (e !is CancellationException) {
                stateListener(UpdatingState.FailedPrepare)
            }
            return@useTemporaryFolder
        }

        try {
            uploadToFlipper(
                scope,
                flipperPath,
                updaterFolder,
                serviceApi.requestApi,
                stateListener
            )
        } catch (e: Throwable) {
            error(e) { "Failed when upload to flipper" }
            if (e !is CancellationException) {
                stateListener(UpdatingState.FailedUpload)
            }
            return@useTemporaryFolder
        }

        stateListener(UpdatingState.Rebooting)
    }

    private suspend fun downloadFirmware(
        updateFile: DistributionFile,
        updaterFolder: File,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        stateListener(UpdatingState.DownloadingFromNetwork(percent = 0.0001f))
        downloaderApi.download(updateFile, updaterFolder, decompress = true).collect {
            when (it) {
                DownloadProgress.Finished ->
                    stateListener(UpdatingState.DownloadingFromNetwork(1.0f))
                is DownloadProgress.InProgress -> stateListener(
                    UpdatingState.DownloadingFromNetwork(
                        it.processedBytes.toFloat() / it.totalBytes.toFloat()
                    )
                )
                DownloadProgress.NotStarted -> stateListener(UpdatingState.NotStarted)
            }
        }
    }

    private suspend fun prepareToUpload(
        updateFile: DistributionFile,
        requestApi: FlipperRequestApi
    ): String {
        var updateName = updateFile.url.substringAfterLast("/").substringBefore(".")
        if (updateName.isBlank()) {
            updateName = updateFile.sha256
        }
        flipperUpdateImageHelper.loadImageOnFlipper(requestApi)
        val flipperPath = "/ext/update/$updateName"

        FolderCreateHelper.mkdirFolderOnFlipper(requestApi, flipperPath)
        return flipperPath
    }

    private suspend fun uploadToFlipper(
        scope: CoroutineScope,
        flipperPath: String,
        updaterFolder: File,
        requestApi: FlipperRequestApi,
        stateListener: suspend (UpdatingState) -> Unit
    ) {
        UploadFirmwareService.upload(
            requestApi,
            updaterFolder,
            flipperPath
        ) { sended, totalBytes ->
            scope.launch(Dispatchers.Default) {
                stateListener(
                    UpdatingState.UploadOnFlipper(
                        sended.toFloat() / totalBytes.toFloat()
                    )
                )
            }
        }

        val response = requestApi.request(
            main {
                systemUpdateRequest = updateRequest {
                    updateManifest = "$flipperPath/update.fuf"
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).first()
        if (response.commandStatus != Flipper.CommandStatus.OK) {
            error("Failed send update request")
        }

        requestApi.requestWithoutAnswer(
            main {
                systemRebootRequest = rebootRequest {
                    mode = System.RebootRequest.RebootMode.UPDATE
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        )
    }
}
