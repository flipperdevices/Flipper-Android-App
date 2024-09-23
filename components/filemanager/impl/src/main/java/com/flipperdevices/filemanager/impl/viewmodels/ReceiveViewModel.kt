package com.flipperdevices.filemanager.impl.viewmodels

import android.content.Context
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.deeplink.model.cleanUp
import com.flipperdevices.filemanager.impl.model.DownloadProgress
import com.flipperdevices.filemanager.impl.model.ShareState
import com.flipperdevices.filemanager.impl.viewmodels.helpers.UploadFileHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class ReceiveViewModel @AssistedInject constructor(
    @Assisted private val path: String,
    @Assisted private val deeplinkContent: DeeplinkContent,
    context: Context,
    serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ReceiveViewModel"

    private val fileName by lazy { deeplinkContent.filename() ?: "Unknown" }
    private val uploadStarted = AtomicBoolean(false)
    private val contentResolver = context.contentResolver
    private val uploadFileHelper = UploadFileHelper(contentResolver)
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
        viewModelScope.launch {
            startUpload(serviceApi)
        }
        viewModelScope.launch {
            serviceApi.requestApi.getSpeed().onEach { serialSpeed ->
                receiveStateFlow.update {
                    it.copy(
                        downloadProgress = it.downloadProgress.updateSpeed(
                            serialSpeed.transmitBytesInSec
                        )
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    private suspend fun startUpload(
        serviceApi: FlipperServiceApi
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (!uploadStarted.compareAndSet(false, true)) {
            info { "Upload file $deeplinkContent in $path already started" }
            return@withContext
        }
        info { "Upload file $deeplinkContent in $path start" }
        val exception = runCatching {
            uploadFileHelper.uploadFile(
                serviceApi.requestApi,
                deeplinkContent,
                File(path, fileName).absolutePath
            ) { delta ->
                if (delta != null) {
                    receiveStateFlow.update {
                        it.copy(
                            downloadProgress = it.downloadProgress.updateProgress(delta)
                        )
                    }
                } else {
                    receiveStateFlow.update { it.copy(processCompleted = true) }
                }
            }
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

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            deeplinkContent: DeeplinkContent,
            path: String
        ): ReceiveViewModel
    }
}
