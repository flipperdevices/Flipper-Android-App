package com.flipperdevices.updater.impl

import android.content.Context
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
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
import com.flipperdevices.updater.model.UpdatingState
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UpdaterTask(
    private val serviceProvider: FlipperServiceProvider,
    private val downloaderApi: DownloaderApi,
    private val context: Context
) : OneTimeExecutionBleTask<DistributionFile, UpdatingState>(serviceProvider),
    LogTagProvider {
    override val TAG = "UpdaterTask"

    private val flipperUpdateImageHelper = FlipperUpdateImageHelper(context)

    private var isRebooting = false

    override suspend fun onStopAsync(stateListener: suspend (UpdatingState) -> Unit) {
        if (!isRebooting) {
            stateListener(UpdatingState.NotStarted)
        }
    }

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: DistributionFile,
        stateListener: suspend (UpdatingState) -> Unit
    ) = FlipperStorageProvider.useTemporaryFolder(context) { tempFolder ->
        info { "Start update with folder: ${tempFolder.absolutePath}" }

        val updaterFolder = File(tempFolder, input.sha256)
        stateListener(UpdatingState.DownloadingFromNetwork(percent = 0.0001f))
        downloaderApi.download(input, updaterFolder, decompress = true).collect {
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

        stateListener(
            UpdatingState.UploadOnFlipper(0f)
        )

        var updateName = input.url.substringAfterLast("/").substringBefore(".")
        if (updateName.isBlank()) {
            updateName = input.sha256
        }
        flipperUpdateImageHelper.loadImageOnFlipper(serviceApi.requestApi)
        val flipperPath = "/ext/update/$updateName"

        FolderCreateHelper.mkdirFolderOnFlipper(serviceApi.requestApi, flipperPath)

        UploadFirmwareService.upload(
            serviceApi.requestApi,
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

        serviceApi.requestApi.request(
            main {
                systemUpdateRequest = updateRequest {
                    updateManifest = "$flipperPath/update.fuf"
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).first()

        serviceApi.requestApi.requestWithoutAnswer(
            main {
                systemRebootRequest = rebootRequest {
                    mode = System.RebootRequest.RebootMode.UPDATE
                }
            }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
        )
        isRebooting = true
        stateListener(UpdatingState.Rebooting)
    }
}
