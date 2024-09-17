package com.flipperdevices.filemanager.impl.viewmodels

import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.readText
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.ProgressListener
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean

private const val LIMITED_SIZE_BYTES = 1024L * 1024L // 1MB

class EditorViewModel @AssistedInject constructor(
    @Assisted private val shareFile: ShareFile,
    context: Context,
    featureProvider: FFeatureProvider,
    storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider {
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
    private val mutex = Mutex()
    private val editorFile by lazy {
        storageProvider.getTemporaryFile()
    }

    init {
        viewModelScope.launch {

            featureProvider.get<FStorageFeatureApi>()
                .collectLatest {
                    onFeatureStateChanged(it)
                }
        }
    }

    fun getEditorState(): StateFlow<EditorState> = editorStateFlow

    private suspend fun onFeatureStateChanged(
        featureStatus: FFeatureStatus<FStorageFeatureApi>
    ) = withLock(mutex, "feature") {
        when (featureStatus) {
            FFeatureStatus.NotFound,
            FFeatureStatus.Unsupported -> editorStateFlow.emit(EditorState.Error)

            FFeatureStatus.Retrieving -> editorStateFlow.emit(EditorState.Loading(DownloadProgress.Infinite()))
            is FFeatureStatus.Supported -> loadFileSafe(featureStatus.featureApi)
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

    private suspend fun loadFileSafe(storageFeatureApi: FStorageFeatureApi) {
        storageFeatureApi.downloadApi().download(
            shareFile.flipperFilePath,
            editorFile
        ) { current ->
            editorStateFlow.emit(EditorState.Loading(DownloadProgress.Fixed()))
        }

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
