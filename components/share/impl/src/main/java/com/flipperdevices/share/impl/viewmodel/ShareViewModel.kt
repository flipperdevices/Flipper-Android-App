package com.flipperdevices.share.impl.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.share.impl.di.ShareComponent
import com.flipperdevices.share.impl.model.DownloadProgress
import com.flipperdevices.share.model.ShareFile
import com.google.protobuf.ByteString
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareViewModel(
    private val shareFile: ShareFile,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ShareViewModel"
    private val downloadStarted = AtomicBoolean(false)
    private val progressState = mutableStateOf(
        DownloadProgress(
            progress = 0,
            totalSize = shareFile.size
        )
    )

    @Inject
    lateinit var provider: FlipperServiceProvider

    init {
        ComponentHolder.component<ShareComponent>().inject(this)
        provider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getDownloadProgress(): State<DownloadProgress> = progressState

    fun cancelDownload() {
        // Not implement yet
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            startDownload(serviceApi)
        }
    }

    private suspend fun startDownload(serviceApi: FlipperServiceApi) = withContext(Dispatchers.IO) {
        if (!downloadStarted.compareAndSet(false, true)) {
            info { "Download file $shareFile already started" }
            return@withContext
        }
        info { "Start download file $shareFile" }
        serviceApi.requestApi.request(getRequest()).collect {
            onFileResponseReceived(it.storageReadResponse.file.data)
        }
    }

    private suspend fun onFileResponseReceived(data: ByteString) = withContext(Dispatchers.IO) {
        incrementProgress(data.size())
    }

    private fun incrementProgress(delta: Int) = synchronized(progressState) {
        val currentProgressState = progressState.value
        progressState.value = currentProgressState
            .copy(progress = currentProgressState.progress + delta)
    }

    private fun getRequest(): FlipperRequest = main {
        storageReadRequest = readRequest {
            path = shareFile.flipperFilePath
        }
    }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
}
