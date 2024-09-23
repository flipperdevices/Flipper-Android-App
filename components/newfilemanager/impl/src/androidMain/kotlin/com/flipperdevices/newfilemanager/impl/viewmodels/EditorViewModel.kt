package com.flipperdevices.newfilemanager.impl.viewmodels

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.limit
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.newfilemanager.impl.model.DownloadProgress
import com.flipperdevices.newfilemanager.impl.model.EditorState
import com.flipperdevices.newfilemanager.impl.model.ShareFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import okio.buffer
import okio.use

private const val LIMITED_SIZE_BYTES = 1024L * 1024L // 1MB

class EditorViewModel @AssistedInject constructor(
    @Assisted private val shareFile: ShareFile,
    private val featureProvider: FFeatureProvider,
    private val storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "EditorViewModel"

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
        viewModelScope.launch {
            val storageApi = featureProvider.getSync<FStorageFeatureApi>()
            if (storageApi == null) {
                editorStateFlow.emit(EditorState.Error)
                return@launch
            }
            uploadFileSafe(storageApi.uploadApi(), textBytes)
        }
    }

    private suspend fun uploadFileSafe(
        uploadApi: FFileUploadApi,
        textBytes: ByteArray
    ) = withLock(mutex, "save") {
        storageProvider.fileSystem.write(editorFile) {
            write(textBytes)
        }

        uploadApi.upload(
            shareFile.flipperFilePath,
            editorFile
        ) { current, max ->
            editorStateFlow.emit(EditorState.Saving(DownloadProgress.Fixed(current, max)))
        }.onFailure { exception ->
            error(exception) { "Failed saved $shareFile" }
            editorStateFlow.emit(EditorState.Error)
        }.onSuccess {
            editorStateFlow.emit(EditorState.Saved)
        }
    }

    private suspend fun loadFileSafe(storageFeatureApi: FStorageFeatureApi) {
        editorStateFlow.emit(EditorState.Loading(DownloadProgress.Infinite()))
        storageFeatureApi.downloadApi().download(
            shareFile.flipperFilePath,
            editorFile
        ) { current, max ->
            editorStateFlow.emit(EditorState.Loading(DownloadProgress.Fixed(current, max)))
        }.onFailure { exception ->
            error(exception) { "Failed download $shareFile" }
            editorStateFlow.emit(EditorState.Error)
        }.onSuccess {
            val content = storageProvider
                .fileSystem
                .source(editorFile)
                .limit(LIMITED_SIZE_BYTES)
                .buffer()
                .use {
                    it.readUtf8()
                }
            val metaSize = storageProvider.fileSystem.metadataOrNull(editorFile)?.size
            val isTooLarge = if (metaSize != null) {
                metaSize > LIMITED_SIZE_BYTES
            } else {
                false
            }
            editorStateFlow.emit(
                EditorState.Loaded(
                    shareFile.flipperFilePath,
                    content = content,
                    tooLarge = isTooLarge
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        storageProvider.fileSystem.delete(editorFile)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            shareFile: ShareFile
        ): EditorViewModel
    }
}
