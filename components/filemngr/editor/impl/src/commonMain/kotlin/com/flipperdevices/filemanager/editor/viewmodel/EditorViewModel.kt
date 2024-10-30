package com.flipperdevices.filemanager.editor.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.limit
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.editor.model.EditorEncodingEnum
import com.flipperdevices.filemanager.editor.model.HexString
import com.flipperdevices.filemanager.editor.util.HexConverter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.Path
import okio.buffer
import okio.use

class EditorViewModel @AssistedInject constructor(
    @Assisted private val path: Path,
    private val featureProvider: FFeatureProvider,
    private val storageProvider: com.flipperdevices.core.FlipperStorageProvider
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "EditorViewModel"

    private val _state = MutableStateFlow<State>(State.Preparing)
    val state = _state.asStateFlow()

    private val mutex = Mutex()

    private val editorFile by lazy { storageProvider.getTemporaryFile() }

    fun getRawContent(): ByteArray? {
        val hexString = (state.value as? EditorViewModel.State.Loaded)?.hexString ?: return null
        return kotlin.runCatching { HexConverter.fromHexString(hexString).content.toByteArray() }
            .onFailure { error(it) { "#onEditorTypeChange could not transform hex" } }
            .getOrNull()
    }

    fun onTextChanged(text: String) {
        _state.update { state ->
            (state as? State.Loaded)?.let { loadedState ->
                when (loadedState.hexString) {
                    is HexString.Hex -> loadedState.copy(hexString = HexString.Hex(text))
                    is HexString.Text -> loadedState.copy(hexString = HexString.Text(text))
                }
            } ?: state
        }
    }

    private suspend fun onFileDownloaded() {
        val content = storageProvider
            .fileSystem
            .source(editorFile)
            .limit(LIMITED_SIZE_BYTES)
            .buffer()
            .use { bufferedSource -> bufferedSource.readUtf8() }
        val metaSize = storageProvider.fileSystem.metadataOrNull(editorFile)?.size
        val isTooLarge = if (metaSize != null) {
            metaSize > LIMITED_SIZE_BYTES
        } else {
            false
        }
        _state.emit(
            State.Loaded(
                path = path,
                hexString = HexString.Text(content),
                isTooLarge = isTooLarge
            )
        )
    }

    private suspend fun loadFileSafe(storageFeatureApi: FStorageFeatureApi) {
        _state.emit(State.Preparing)
        storageFeatureApi.downloadApi().download(
            pathOnFlipper = path.toString(),
            fileOnAndroid = editorFile,
            progressListener = { current, max ->
                _state.emit(
                    State.Loading(
                        downloaded = current,
                        total = max
                    )
                )
            }
        ).onFailure { exception ->
            error(exception) { "Failed download $path" }
            _state.emit(State.Error)
        }.onSuccess {
            onFileDownloaded()
        }
    }

    private suspend fun onFeatureStateChanged(
        featureStatus: FFeatureStatus<FStorageFeatureApi>
    ) = withLock(mutex, "feature") {
        when (featureStatus) {
            FFeatureStatus.NotFound,
            FFeatureStatus.Unsupported -> _state.emit(State.Error)

            FFeatureStatus.Retrieving -> _state.emit(State.Preparing)

            is FFeatureStatus.Supported -> loadFileSafe(featureStatus.featureApi)
        }
    }

    init {
        featureProvider.get<FStorageFeatureApi>()
            .onEach { onFeatureStateChanged(it) }
            .launchIn(viewModelScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        storageProvider.fileSystem.delete(editorFile)
    }

    fun onEditorTypeChange(type: EditorEncodingEnum) {
        _state.update { state ->
            val loaded = (state as? State.Loaded) ?: return@update state
            if (loaded.encoding == type) return@update state
            // We can't always transform hex into string
            // As example, user can write letters beyond 0..F
            val hexStringResult = kotlin.runCatching {
                when (type) {
                    EditorEncodingEnum.TEXT -> {
                        HexConverter.fromHexString(loaded.hexString)
                    }

                    EditorEncodingEnum.HEX -> {
                        HexConverter.toHexString(loaded.hexString)
                    }
                }
            }.onFailure { error(it) { "#onEditorTypeChange could not transform hex" } }

            loaded.copy(
                encoding = type
                    .takeIf { hexStringResult.isSuccess }
                    ?: loaded.encoding,
                hexString = hexStringResult.getOrElse { loaded.hexString }
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(path: Path): EditorViewModel
    }

    sealed interface State {
        data object Preparing : State

        data class Loading(
            val downloaded: Long,
            val total: Long
        ) : State

        data class Saving(
            val uploaded: Long,
            val total: Long
        ) : State

        data class Loaded(
            val path: Path,
            val hexString: HexString,
            val isTooLarge: Boolean,
            val encoding: EditorEncodingEnum = EditorEncodingEnum.TEXT
        ) : State

        data object Error : State
    }

    companion object {
        private const val LIMITED_SIZE_BYTES = 1024L * 1024L // 1MB
    }
}
