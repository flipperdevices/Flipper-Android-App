package com.flipperdevices.filemanager.receive.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.receive.di.ShareReceiveComponent
import com.flipperdevices.filemanager.sharecommon.model.DownloadProgress
import com.flipperdevices.filemanager.sharecommon.model.ShareState
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import java.io.File
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiveViewModel(
    private val deeplinkContent: DeeplinkContent,
    private val flipperPath: String,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ReceiveViewModel"

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    private val uploadStarted = AtomicBoolean(false)
    private val contentResolver = application.contentResolver
    private val receiveStateFlow = MutableStateFlow(
        ShareState(
            deeplinkContent.length()?.let {
                DownloadProgress.Fixed(0L, it)
            } ?: DownloadProgress.Infinite(0L)
        )
    )

    init {
        ComponentHolder.component<ShareReceiveComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getReceiveState(): StateFlow<ShareState> = receiveStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            startUpload(serviceApi)
        }
        viewModelScope.launch {
            serviceApi.requestApi.getSpeed().collect { serialSpeed ->
                receiveStateFlow.update {
                    it.copy(
                        downloadProgress = it.downloadProgress.updateSpeed(
                            serialSpeed.transmitBytesInSec
                        )
                    )
                }
            }
        }
    }

    fun cancelUpload() {
        receiveStateFlow.update {
            it.copy(dialogShown = false)
        }
    }

    private suspend fun startUpload(serviceApi: FlipperServiceApi) = withContext(Dispatchers.IO) {
        if (!uploadStarted.compareAndSet(false, true)) {
            info { "Upload file $deeplinkContent already started" }
            return@withContext
        }
        info { "Upload file $deeplinkContent start" }
        val exception = runCatching {
            deeplinkContent.openStream().use { fileStream ->
                val stream = fileStream ?: return@use
                val filePath =
                    File(flipperPath, deeplinkContent.filename() ?: "Unknown").absolutePath
                val requestFlow = streamToCommandFlow(
                    stream, deeplinkContent.length()
                ) { chunkData ->
                    storageWriteRequest = writeRequest {
                        path = filePath
                        file = file { data = chunkData }
                    }
                }.map {
                    it.wrapToRequest(FlipperRequestPriority.FOREGROUND)
                }
                val response = serviceApi.requestApi.request(requestFlow)
                info { "File send with response $response" }
            }
        }.exceptionOrNull()
        cleanUp()
        receiveStateFlow.update {
            it.copy(
                dialogShown = false,
                processCompleted = true
            )
        }
        if (exception != null) {
            error(exception) { "Can't upload $deeplinkContent" }
        }
    }

    private fun DeeplinkContent.openStream(): InputStream? {
        return when (this) {
            is DeeplinkContent.ExternalUri -> {
                contentResolver.openInputStream(uri)
            }
            is DeeplinkContent.InternalStorageFile -> {
                file.inputStream()
            }
            is DeeplinkContent.FFFContent -> content.stream()
        }
    }

    private fun cleanUp() {
        when (deeplinkContent) {
            is DeeplinkContent.ExternalUri -> {
                contentResolver.releasePersistableUriPermission(
                    deeplinkContent.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            is DeeplinkContent.InternalStorageFile -> {
                deeplinkContent.file.delete()
            }
            is DeeplinkContent.FFFContent -> {} // Noting
        }
    }
}
