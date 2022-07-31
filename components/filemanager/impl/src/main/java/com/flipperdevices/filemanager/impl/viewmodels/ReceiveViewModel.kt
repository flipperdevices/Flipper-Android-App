package com.flipperdevices.filemanager.impl.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.impl.api.CONTENT_KEY
import com.flipperdevices.filemanager.impl.api.PATH_KEY
import com.flipperdevices.filemanager.sharecommon.model.DownloadProgress
import com.flipperdevices.filemanager.sharecommon.model.ShareState
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class ReceiveViewModel @VMInject constructor(
    @TangleParam(PATH_KEY)
    encodedPath: String,
    @TangleParam(CONTENT_KEY)
    private val deeplinkContent: DeeplinkContent,
    context: Context,
    serviceProvider: FlipperServiceProvider
) : LifecycleViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ReceiveViewModel"

    private val path = URLDecoder.decode(encodedPath, "UTF-8")

    private val fileName by lazy { deeplinkContent.filename() ?: "Unknown" }
    private val uploadStarted = AtomicBoolean(false)
    private val contentResolver = context.contentResolver
    private val receiveStateFlow = MutableStateFlow(
        ShareState(
            fileName,
            deeplinkContent.length()?.let {
                DownloadProgress.Fixed(totalSize = it)
            } ?: DownloadProgress.Infinite()
        )
    )

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getReceiveState(): StateFlow<ShareState> = receiveStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch(Dispatchers.Default) {
            startUpload(serviceApi)
        }
        viewModelScope.launch(Dispatchers.Default) {
            serviceApi.requestApi.getSpeed().onEach { serialSpeed ->
                receiveStateFlow.update {
                    it.copy(
                        downloadProgress = it.downloadProgress.updateSpeed(
                            serialSpeed.transmitBytesInSec
                        )
                    )
                }
            }.launchIn(viewModelScope + Dispatchers.Default)
        }
    }

    private suspend fun startUpload(serviceApi: FlipperServiceApi) = withContext(Dispatchers.IO) {
        if (!uploadStarted.compareAndSet(false, true)) {
            info { "Upload file $deeplinkContent in $path already started" }
            return@withContext
        }
        info { "Upload file $deeplinkContent in $path start" }
        val exception = runCatching {
            uploadToFlipper(serviceApi.requestApi)
        }.exceptionOrNull()
        deeplinkContent.cleanUp(contentResolver)
        receiveStateFlow.update {
            it.copy(
                processCompleted = true
            )
        }
        if (exception != null) {
            error(exception) { "Can't upload $deeplinkContent" }
        }
    }

    private suspend fun uploadToFlipper(requestApi: FlipperRequestApi) {
        deeplinkContent.openStream(contentResolver).use { fileStream ->
            val stream = fileStream ?: return@use
            val filePath = File(path, fileName).absolutePath
            val requestFlow = streamToCommandFlow(
                stream,
                deeplinkContent.length()
            ) { chunkData ->
                storageWriteRequest = writeRequest {
                    path = filePath
                    file = file { data = chunkData }
                }
            }.map { message ->
                FlipperRequest(
                    data = message,
                    priority = FlipperRequestPriority.FOREGROUND,
                    onSendCallback = {
                        receiveStateFlow.update {
                            it.copy(
                                downloadProgress = it.downloadProgress.updateProgress(
                                    message.storageWriteRequest.file.data.size().toLong()
                                )
                            )
                        }
                    }
                )
            }
            val response = requestApi.request(requestFlow, onCancel = { id ->
                requestApi.request(
                    main {
                        commandId = id
                        hasNext = false
                        storageWriteRequest = writeRequest {
                            path = filePath
                        }
                    }.wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
                ).collect()
                receiveStateFlow.update {
                    it.copy(processCompleted = true)
                }
            })
            info { "File send with response $response" }
        }
    }
}
