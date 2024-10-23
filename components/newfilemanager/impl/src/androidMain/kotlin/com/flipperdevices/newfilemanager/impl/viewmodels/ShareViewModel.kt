package com.flipperdevices.newfilemanager.impl.viewmodels

import android.app.Application
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.share.SharableFile
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.newfilemanager.impl.R
import com.flipperdevices.newfilemanager.impl.model.DownloadProgress
import com.flipperdevices.newfilemanager.impl.model.ShareFile
import com.flipperdevices.newfilemanager.impl.model.ShareState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okio.Path.Companion.toOkioPath

class ShareViewModel @AssistedInject constructor(
    private val featureProvider: FFeatureProvider,
    @Assisted private val shareFile: ShareFile,
    private val application: Application
) : CommonShareViewModel(
    featureProvider = featureProvider,
    fileName = shareFile.name,
    defaultProgress = DownloadProgress.Fixed(
        totalSize = shareFile.size
    )
),
    LogTagProvider {
    override val TAG = "ShareViewModel"
    private val fileInSharedDir by lazy {
        SharableFile(application, shareFile.name).apply {
            createClearNewFileWithMkDirs()
        }
    }

    override suspend fun start(
        storageFeatureApi: FStorageFeatureApi
    ): Unit = withContext(FlipperDispatchers.workStealingDispatcher) {
        info { "Start download file $shareFile" }
        storageFeatureApi.downloadApi().download(
            shareFile.flipperFilePath,
            fileInSharedDir.toOkioPath()
        ) { current, max ->
            shareStateFlow.emit(
                ShareState.Ready(
                    name = fileName,
                    downloadProgress = DownloadProgress.Fixed(current, max)
                )
            )
        }.onFailure { exception ->
            error(exception) { "Can't download $shareFile" }
            shareStateFlow.emit(ShareState.Error)
        }.onSuccess {
            onCompleteDownload()
        }
    }

    private suspend fun onCompleteDownload() = withContext(Dispatchers.Main) {
        ShareHelper.shareFile(
            context = application,
            file = fileInSharedDir,
            resId = R.string.share_picker_title
        )
        shareStateFlow.update {
            if (it is ShareState.Ready) {
                it.copy(processCompleted = true)
            } else {
                it
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            shareFile: ShareFile
        ): ShareViewModel
    }
}
