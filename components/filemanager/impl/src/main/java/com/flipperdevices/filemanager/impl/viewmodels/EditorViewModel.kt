package com.flipperdevices.filemanager.impl.viewmodels

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.readText
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.impl.model.DownloadProgress
import com.flipperdevices.filemanager.impl.model.EditorState
import com.flipperdevices.filemanager.impl.model.ShareFile
import com.flipperdevices.filemanager.impl.viewmodels.helpers.DownloadFileHelper
import com.flipperdevices.filemanager.impl.viewmodels.helpers.UploadFileHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean

private const val LIMITED_SIZE_BYTES = 1024L * 1024L // 1MB

class EditorViewModel @AssistedInject constructor(
    @Assisted private val shareFile: ShareFile,
    context: Context,
    private val serviceProvider: FlipperServiceProvider,
    storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), FlipperBleServiceConsumer, LogTagProvider {
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
        storageProvider.getTemporaryFile().toFile()
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

    override fun onDestroy() {
        super.onDestroy()
        editorFile.delete()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            shareFile: ShareFile
        ): EditorViewModel
    }
}
