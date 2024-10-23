package com.flipperdevices.filemanager.upload.impl.viewmodel

import android.content.Context
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.deeplink.model.openStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import okio.Path
import okio.source
import javax.inject.Inject

class UploadViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
    context: Context,
) : DecomposeViewModel(), LogTagProvider {

    private val mutex = Mutex()

    override val TAG: String = "UploadViewModel"

    private val contentResolver = context.contentResolver

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    val speedState = featureProvider.get<FSpeedFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)
        .filterIsInstance<FFeatureStatus.Supported<FSpeedFeatureApi>>()
        .map { it.featureApi }
        .map { it.getSpeed().value.transmitBytesInSec }

    private var lastJob: Job? = null

    fun onCancel() {
        _state.update { State.Cancelled }
    }

    private suspend fun uploadFile(
        uploadApi: FFileUploadApi,
        deeplinkContent: DeeplinkContent,
        folderPath: Path,
        currentFileIndex: Int,
        totalFilesAmount: Int
    ) {
        val fileStream = deeplinkContent.openStream(contentResolver) ?: run {
            _state.emit(State.Error)
            return
        }
        val totalLength = deeplinkContent.length() ?: run {
            _state.emit(State.Error)
            return
        }
        val fileName = deeplinkContent.filename() ?: run {
            _state.emit(State.Error)
            return
        }
        val pathOnFlipper = folderPath.resolve(fileName)
        runCatching {
            fileStream.use { outputStream ->
                uploadApi.sink(pathOnFlipper.toString())
                    .use { sink ->
                        outputStream.source().copyWithProgress(
                            sink = sink,
                            sourceLength = {
                                deeplinkContent.length()
                            },
                            progressListener = { current, max ->
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
                    }
            }
        }.onFailure { it.printStackTrace() }
    }

    fun tryUpload(
        path: Path,
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
                launchWithLock(mutex, viewModelScope, "upload") {
                    lastJob?.cancelAndJoin()
                    lastJob = launch {
                        contents.forEachIndexed { fileIndex, deeplinkContent ->
                            uploadFile(
                                uploadApi = uploadApi,
                                deeplinkContent = deeplinkContent,
                                folderPath = path,
                                currentFileIndex = fileIndex,
                                totalFilesAmount = totalFilesAmount
                            )
                        }
                        _state.emit(State.Uploaded)
                    }
                }
            }.launchIn(viewModelScope)
    }

    sealed interface State {
        data object Pending : State
        data object Error : State
        data object Uploaded : State
        data object Cancelled : State
        data class Uploading(
            val fileIndex: Int,
            val totalFiles: Int,
            val uploadedFileSize: Long,
            val uploadFileTotalSize: Long,
            val fileName: String
        ) : State
    }
}
