package com.flipperdevices.filemanager.impl.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.readText
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.impl.api.FILE_PATH_KEY
import com.flipperdevices.filemanager.impl.model.DownloadProgress
import com.flipperdevices.filemanager.impl.model.EditorState
import com.flipperdevices.filemanager.impl.model.ShareFile
import com.flipperdevices.filemanager.impl.viewmodels.helpers.DownloadFileHelper
import com.flipperdevices.filemanager.impl.viewmodels.helpers.UploadFileHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject
import java.util.concurrent.atomic.AtomicBoolean

private const val LIMITED_SIZE_BYTES = 1024L * 1024L // 1MB

class EditorViewModel @VMInject constructor(
    @TangleParam(FILE_PATH_KEY)
    private val shareFile: ShareFile,
    context: Context,
    private val serviceProvider: FlipperServiceProvider
) : LifecycleViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    override val TAG = "EditorViewModel"

    private val downloadFileHelper = DownloadFileHelper()
    private val uploadFileHelper = UploadFileHelper(context.contentResolver)
    private val editorStateFlow = MutableStateFlow<EditorState>(
        EditorState.Loading(
            progress = DownloadProgress.Fixed(
                totalSize = shareFile.size
            )
        )
    )
    private val alreadyStarted = AtomicBoolean(false)
    private val mutex = Mutex()
    private val editorFile by lazy {
        FlipperStorageProvider.getTemporaryFile(context)
    }

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getEditorState(): StateFlow<EditorState> = editorStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        if (alreadyStarted.compareAndSet(false, true)) {
            info { "Editor with path $shareFile already started" }
        }

        launchWithLock(mutex, viewModelScope, "load") {
            loadFileSafe(serviceApi.requestApi)
        }
    }

    fun onSaveFile(text: String) {
        val textBytes = text.toByteArray()
        editorStateFlow.update {
            EditorState.Saving(
                DownloadProgress.Fixed(
                    totalSize = textBytes.size.toLong()
                )
            )
        }
        serviceProvider.provideServiceApi(this) { serviceApi ->
            launchWithLock(mutex, viewModelScope, "save") {
                uploadFileSafe(serviceApi.requestApi, textBytes)
            }
        }
    }

    private suspend fun uploadFileSafe(
        requestApi: FlipperRequestApi,
        textBytes: ByteArray
    ) {
        editorFile.writeBytes(textBytes)

        val exception = runCatching {
            uploadFileHelper.uploadFile(
                requestApi,
                DeeplinkContent.InternalStorageFile(editorFile.absolutePath),
                shareFile.flipperFilePath
            ) { delta ->
                if (delta != null) {
                    editorStateFlow.update {
                        if (it is EditorState.Saving) {
                            it.copy(progress = it.progress.updateProgress(delta))
                        } else {
                            it
                        }
                    }
                } else {
                    editorStateFlow.update { EditorState.Saved }
                }
            }
        }.exceptionOrNull()

        if (exception != null) {
            error(exception) { "Failed saved $shareFile" }
            return
        }

        editorStateFlow.update { EditorState.Saved }
    }

    private suspend fun loadFileSafe(requestApi: FlipperRequestApi) {
        val exception = runCatching {
            downloadFileHelper.downloadFile(
                requestApi,
                shareFile.flipperFilePath,
                editorFile
            ) { delta ->
                editorStateFlow.update {
                    if (it is EditorState.Loading) {
                        it.copy(
                            progress = it.progress.updateProgress(delta)
                        )
                    } else {
                        it
                    }
                }
            }
        }.exceptionOrNull()

        if (exception != null) {
            error(exception) { "Failed download $shareFile" }
            editorStateFlow.update { EditorState.Saved }
            return
        }

        editorStateFlow.update {
            EditorState.Loaded(
                shareFile.flipperFilePath,
                editorFile.readText(LIMITED_SIZE_BYTES),
                tooLarge = editorFile.length() > LIMITED_SIZE_BYTES
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        editorFile.delete()
    }
}
