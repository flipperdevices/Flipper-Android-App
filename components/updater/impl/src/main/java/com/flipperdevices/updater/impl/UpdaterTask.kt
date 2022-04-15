package com.flipperdevices.updater.impl

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.ui.TaskWithLifecycle
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.UpdatingState
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdaterTask(
    private val serviceProvider: FlipperServiceProvider,
    private val downloaderApi: DownloaderApi,
    private val context: Context
) : TaskWithLifecycle(), LogTagProvider {
    override val TAG = "UpdaterTask"

    private val taskScope = lifecycleScope

    fun start(
        updateFile: DistributionFile,
        onStateUpdate: suspend (UpdatingState) -> Unit
    ) = taskScope.launch(Dispatchers.Main) {
        info { "Start updating" }
        serviceProvider.provideServiceApi(this@UpdaterTask) { serviceApi ->
            info { "Flipper service provided" }
            taskScope.launch(Dispatchers.Default) {
                // Waiting to be connected to the flipper
                serviceApi.connectionInformationApi.getConnectionStateFlow()
                    .collectLatest {
                        if (it is ConnectionState.Ready && it.isSupported) {
                            try {
                                startInternal(updateFile, serviceApi, onStateUpdate)
                            } catch (
                                @Suppress("TooGenericExceptionCaught")
                                throwable: Throwable
                            ) {
                                error(throwable) { "Error during updating" }
                            }
                        }
                    }
            }
        }
        taskScope.launch(Dispatchers.Main) {
            onStart()
        }
        taskScope.launch(Dispatchers.Default) {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    onStateUpdate(UpdatingState.NotStarted)
                }
            }
        }
    }

    private suspend fun startInternal(
        updateFile: DistributionFile,
        serviceApi: FlipperServiceApi,
        onStateUpdate: suspend (UpdatingState) -> Unit
    ) = FlipperStorageProvider.useTemporaryFolder(context) { tempFolder ->
        val updaterFolder = File(tempFolder, updateFile.sha256)
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
    }
}
