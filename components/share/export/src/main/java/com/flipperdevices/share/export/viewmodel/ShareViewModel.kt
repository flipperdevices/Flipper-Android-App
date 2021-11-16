package com.flipperdevices.share.export.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.createClearFileInCacheSafe
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.share.common.model.DownloadProgress
import com.flipperdevices.share.common.model.ShareState
import com.flipperdevices.share.export.BuildConfig
import com.flipperdevices.share.export.R
import com.flipperdevices.share.export.di.ShareComponent
import com.flipperdevices.share.model.ShareFile
import com.google.protobuf.ByteString
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SHARE_DIR = "sharedkeys/"

class ShareViewModel(
    private val shareFile: ShareFile,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ShareViewModel"
    private val downloadStarted = AtomicBoolean(false)
    private val fileInSharedDir = application.createClearFileInCacheSafe(
        SHARE_DIR,
        shareFile.name
    )
    private val shareStateFlow = MutableStateFlow(
        ShareState(
            DownloadProgress.Fixed(
                progress = 0,
                totalSize = shareFile.size
            )
        )
    )

    @Inject
    lateinit var provider: FlipperServiceProvider

    init {
        ComponentHolder.component<ShareComponent>().inject(this)
        provider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getShareState(): StateFlow<ShareState> = shareStateFlow

    fun cancelDownload() {
        shareStateFlow.update { it.copy(dialogShown = false) }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            startDownload(serviceApi)
        }
        viewModelScope.launch {
            serviceApi.requestApi.getSpeed().collect { serialSpeed ->
                shareStateFlow.update {
                    it.copy(
                        downloadProgress = it.downloadProgress.updateSpeed(
                            serialSpeed.receiveBytesInSec
                        )
                    )
                }
            }
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
            if (!it.hasNext) {
                onCompleteDownload()
            }
        }
    }

    private suspend fun onCompleteDownload() = withContext(Dispatchers.Main) {
        if (!shareStateFlow.value.dialogShown) {
            info { "Not share file, because dialog closed before" }
            return@withContext
        }

        val context = getApplication<Application>()
        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.SHARE_FILE_AUTHORITIES,
            fileInSharedDir,
            shareFile.name
        )
        val intent = Intent(Intent.ACTION_SEND, uri).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val activityIntent = Intent.createChooser(
            intent,
            context.getString(R.string.share_picker_title, shareFile.name)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(activityIntent)

        shareStateFlow.update {
            it.copy(
                dialogShown = false,
                processCompleted = true
            )
        }
    }

    private suspend fun onFileResponseReceived(data: ByteString) = withContext(Dispatchers.IO) {
        incrementProgress(data.size())
        fileInSharedDir.appendBytes(data.toByteArray())
    }

    private fun incrementProgress(delta: Int) {
        shareStateFlow.update {
            val progress = it.downloadProgress
            it.copy(
                downloadProgress = progress.updateProgress(delta.toLong())
            )
        }
    }

    private fun getRequest(): FlipperRequest = main {
        storageReadRequest = readRequest {
            path = shareFile.flipperFilePath
        }
    }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
}
