package com.flipperdevices.updater.impl

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.ui.TaskWithLifecycle
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.System
import com.flipperdevices.protobuf.system.rebootRequest
import com.flipperdevices.protobuf.system.updateRequest
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.impl.service.UploadFirmwareService
import com.flipperdevices.updater.impl.utils.FolderCreateHelper
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.UpdatingState
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class UpdaterTask(
    private val serviceProvider: FlipperServiceProvider,
    private val downloaderApi: DownloaderApi,
    private val context: Context
) : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "UpdaterTask"

    private val taskScope = lifecycleScope
    private val mutex = Mutex()
    private var updaterJob: Job? = null
    private var isRebooting = false

    fun start(
        updateFile: DistributionFile,
        onStateUpdate: suspend (UpdatingState) -> Unit
    ) = taskScope.launch(Dispatchers.Main) {
        info { "Start updating" }
        serviceProvider.provideServiceApi(this@UpdaterTask) { serviceApi ->
            info { "Flipper service provided" }
            launchWithLock(mutex, taskScope) {
                updaterJob?.cancelAndJoin()
                updaterJob = null
                updaterJob = taskScope.launch(Dispatchers.Default) {
                    // Waiting to be connected to the flipper
                    try {
                        startInternal(updateFile, serviceApi, onStateUpdate)
                        onStop()
                    } catch (
                        @Suppress("TooGenericExceptionCaught")
                        throwable: Throwable
                    ) {
                        error(throwable) { "Error during updating" }
                        onStop()
                    }
                }
            }
        }
        onStart()
        taskScope.launch(Dispatchers.Default) {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    if (!isRebooting) {
                        onStateUpdate(UpdatingState.NotStarted)
                    }
                }
            }
        }
    }

    private suspend fun startInternal(
        updateFile: DistributionFile,
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (UpdatingState) -> Unit
    ) = FlipperStorageProvider.useTemporaryFolder(context) { tempFolder ->
        info { "Start update with folder: ${tempFolder.absolutePath}" }
        val updaterFolder = File(tempFolder, updateFile.sha256)
        onStateUpdate(UpdatingState.DownloadingFromNetwork(percent = 0.0001f))
        downloaderApi.download(updateFile, updaterFolder, decompress = true).collect {
            when (it) {
                DownloadProgress.Finished ->
                    onStateUpdate(UpdatingState.DownloadingFromNetwork(1.0f))
                is DownloadProgress.InProgress -> onStateUpdate(
                    UpdatingState.DownloadingFromNetwork(
                        it.processedBytes.toFloat() / it.totalBytes.toFloat()
                    )
                )
                DownloadProgress.NotStarted -> onStateUpdate(UpdatingState.NotStarted)
            }
        }

        onStateUpdate(
            UpdatingState.UploadOnFlipper(0f)
        )

        var updateName = updateFile.url.substringAfterLast("/").substringBefore(".")
        if (updateName.isBlank()) {
            updateName = updateFile.sha256
        }
        val flipperPath = "/ext/update/$updateName"

        FolderCreateHelper.recreateDirOnFlipper(serviceApi.requestApi, flipperPath)

        UploadFirmwareService.upload(
            serviceApi.requestApi,
            updaterFolder,
            flipperPath
        ) { sended, totalBytes ->
            taskScope.launch(Dispatchers.Default) {
                onStateUpdate(
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
        onStateUpdate(UpdatingState.Rebooting)
    }
}
