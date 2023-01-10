package com.flipperdevices.filemanager.impl.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.share.SharableFile
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.api.FILE_PATH_KEY
import com.flipperdevices.filemanager.impl.model.DownloadProgress
import com.flipperdevices.filemanager.impl.model.ShareFile
import com.flipperdevices.filemanager.impl.model.ShareState
import com.flipperdevices.filemanager.impl.viewmodels.helpers.DownloadFileHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject
import java.util.concurrent.atomic.AtomicBoolean

class ShareViewModel @VMInject constructor(
    flipperServiceProvider: FlipperServiceProvider,
    @TangleParam(FILE_PATH_KEY)
    private val shareFile: ShareFile,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "ShareViewModel"
    private val downloadStarted = AtomicBoolean(false)
    private val downloadFileHelper = DownloadFileHelper()
    private val fileInSharedDir by lazy {
        SharableFile(application, shareFile.name).apply {
            createClearNewFileWithMkDirs()
        }
    }
    private val shareStateFlow = MutableStateFlow(
        ShareState(
            shareFile.name,
            DownloadProgress.Fixed(
                totalSize = shareFile.size
            )
        )
    )

    init {
        flipperServiceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getShareState(): StateFlow<ShareState> = shareStateFlow

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
        val exception = runCatching {
            downloadFileHelper.downloadFile(
                serviceApi.requestApi,
                shareFile.flipperFilePath,
                fileInSharedDir
            ) { delta ->
                shareStateFlow.update {
                    it.copy(
                        downloadProgress = it.downloadProgress.updateProgress(delta)
                    )
                }
            }
        }.exceptionOrNull()

        if (exception != null) {
            shareStateFlow.update {
                it.copy(
                    processCompleted = true
                )
            }
            error(exception) { "Can't download $shareFile" }
        } else {
            onCompleteDownload()
        }
    }

    private suspend fun onCompleteDownload() = withContext(Dispatchers.Main) {
        ShareHelper.shareFile(
            context = getApplication<Application>(),
            file = fileInSharedDir,
            resId = R.string.share_picker_title
        )
        shareStateFlow.update {
            it.copy(
                processCompleted = true
            )
        }
    }
}
