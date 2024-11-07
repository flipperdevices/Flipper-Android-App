package com.flipperdevices.filemanager.upload.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent.State
import com.flipperdevices.filemanager.upload.impl.deeplink.DeeplinkContentProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.Path
import javax.inject.Inject

class UploadViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
    private val deeplinkContentProvider: DeeplinkContentProvider,
    private val storageProvider: FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider {

    private val mutex = Mutex()

    override val TAG: String = "UploadViewModel"

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    val speedState: Flow<Long> = featureProvider.get<FSpeedFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)
        .filterIsInstance<FFeatureStatus.Supported<FSpeedFeatureApi>>()
        .map { it.featureApi }
        .map { it.getSpeed().value.transmitBytesInSec }

    private var lastJob: Job? = null

    fun onCancel() {
        viewModelScope.launch {
            lastJob?.cancelAndJoin()
            _state.update { State.Cancelled }
        }
    }

    @Suppress("LongParameterList")
    private suspend fun uploadFile(
        uploadApi: FFileUploadApi,
        deeplinkContent: DeeplinkContent,
        folderPath: Path,
        fileName: String? = deeplinkContent.filename(),
        currentFileIndex: Int,
        totalFilesAmount: Int
    ): Result<ListingItem> {
        return runCatching {
            fileName ?: error("#uploadFile fileName is null")
            val fileStream = deeplinkContentProvider
                .source(deeplinkContent)
                ?: error("#uploadFile could not get deeplink source")
            val totalLength = deeplinkContent
                .length()
                ?: error("#uploadFile could not get deeplink totalLength")

            val pathOnFlipper = folderPath.resolve(fileName)
            fileStream.use { deeplinkSource ->
                uploadApi.sink(pathOnFlipper.toString())
                    .use { sink ->
                        deeplinkSource.copyWithProgress(
                            sink = sink,
                            sourceLength = {
                                deeplinkContent.length()
                            },
                            progressListener = { current, max ->
                                if (!currentCoroutineContext().isActive) {
                                    sink.close()
                                    deeplinkSource.close()
                                    return@copyWithProgress
                                }
                                val progress = when (max) {
                                    0L -> 0f
                                    else -> current / max.toFloat()
                                }
                                _state.update {
                                    State.Uploading(
                                        fileIndex = currentFileIndex,
                                        totalFiles = totalFilesAmount,
                                        uploadedFileSize = progress
                                            .times(totalLength)
                                            .toLong(),
                                        uploadFileTotalSize = totalLength,
                                        fileName = fileName
                                    )
                                }
                            }
                        )
                        ListingItem(
                            fileName = fileName,
                            fileType = FileType.FILE,
                            size = totalLength
                        )
                    }
            }
        }.onFailure { throwable -> error(throwable) { "#uploadFile could not upload file" } }
    }

    fun uploadRaw(
        folderPath: Path,
        fileName: String,
        content: ByteArray,
    ) {
        val temporaryFile = storageProvider.getTemporaryFile()
        storageProvider.fileSystem.write(temporaryFile) {
            write(content)
        }
        val deeplinkContent = DeeplinkContent.InternalStorageFile(
            filePath = temporaryFile.toString()
        )
        _state.update {
            State.Uploading(
                fileIndex = 0,
                totalFiles = 1,
                uploadedFileSize = 0L,
                uploadFileTotalSize = 0L,
                fileName = fileName
            )
        }
        featureProvider.get<FStorageFeatureApi>()
            .onEach {
                if (it !is FFeatureStatus.Supported<FStorageFeatureApi>) {
                    _state.emit(State.Error)
                    return@onEach
                }
                val storageApi = it.featureApi
                val uploadApi = storageApi.uploadApi()
                viewModelScope.launch {
                    mutex.withLock("uploadRaw") {
                        lastJob?.cancelAndJoin()
                        lastJob = launch {
                            val uploadedItems = uploadFile(
                                uploadApi = uploadApi,
                                deeplinkContent = deeplinkContent,
                                fileName = fileName,
                                folderPath = folderPath,
                                currentFileIndex = 0,
                                totalFilesAmount = 1
                            ).getOrNull().let(::listOf).filterNotNull()
                            storageProvider.fileSystem.delete(temporaryFile)
                            _state.emit(State.Uploaded(uploadedItems))
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun tryUpload(
        folderPath: Path,
        contents: List<DeeplinkContent>
    ) {
        if (contents.isEmpty()) {
            _state.update { State.Cancelled }
            return
        }
        val totalFilesAmount = contents.size
        _state.update {
            State.Uploading(
                fileIndex = 0,
                totalFiles = totalFilesAmount,
                uploadedFileSize = 0L,
                uploadFileTotalSize = 0L,
                fileName = contents.firstOrNull()?.filename().orEmpty()
            )
        }
        featureProvider.get<FStorageFeatureApi>()
            .onEach {
                if (it !is FFeatureStatus.Supported<FStorageFeatureApi>) {
                    _state.emit(State.Error)
                    return@onEach
                }
                val storageApi = it.featureApi
                val uploadApi = storageApi.uploadApi()
                viewModelScope.launch {
                    mutex.withLock("tryUpload") {
                        lastJob?.cancelAndJoin()
                        lastJob = launch {
                            val uploadedItems = contents.mapIndexed { fileIndex, deeplinkContent ->
                                uploadFile(
                                    uploadApi = uploadApi,
                                    deeplinkContent = deeplinkContent,
                                    folderPath = folderPath,
                                    currentFileIndex = fileIndex,
                                    totalFilesAmount = totalFilesAmount
                                ).getOrNull()
                            }.filterNotNull()

                            _state.emit(State.Uploaded(uploadedItems))
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
