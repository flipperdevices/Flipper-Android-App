package com.flipperdevices.filemanager.upload.impl.viewmodel

import android.content.Context
import android.util.Log
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.deeplink.model.openStream
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.Path
import okio.source
import kotlin.io.path.Path
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

    fun onCancel() {
        _state.update { State.Cancelled }
    }

    fun startUpload(
        path: Path,
        contents: List<DeeplinkContent>
    ) {
        if (contents.isEmpty()) return
        val totalFiles = contents.size
        _state.update {
            State.Uploading(
                fileIndex = 0,
                totalFiles = totalFiles,
                uploadedFileSize = 0L,
                uploadFileTotalSize = 0L,
                fileName = contents.firstOrNull()?.filename().orEmpty()
            )
        }
        launchWithLock(mutex, viewModelScope, "upload") {
            val storageApi = featureProvider.getSync<FStorageFeatureApi>() ?: run {
                _state.emit(State.Error)
                return@launchWithLock
            }
            val uploadApi = storageApi.uploadApi()
            contents.forEachIndexed { fileIndex, deeplinkContent ->
                val fileStream = deeplinkContent.openStream(contentResolver) ?: run {
                    _state.emit(State.Error)
                    return@forEachIndexed
                }
                val totalLength = deeplinkContent.length() ?: run {
                    _state.emit(State.Error)
                    return@forEachIndexed
                }
                val fileName = deeplinkContent.filename() ?: run {
                    _state.emit(State.Error)
                    return@forEachIndexed
                }
                val pathOnFlipper = path.resolve(fileName).toString()
                runCatching {
                    Log.d("MAKEEVRSERG", "startUpload: $pathOnFlipper")
                    fileStream.use { outputStream ->
                        Log.d("MAKEEVRSERG", "using outputStream")
                        uploadApi.sink(pathOnFlipper)
                            .use { sink ->
                                Log.d("MAKEEVRSERG", "using sink flipper")
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
                                                fileIndex = fileIndex,
                                                totalFiles = totalFiles,
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
            _state.emit(State.Uploaded)
        }
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