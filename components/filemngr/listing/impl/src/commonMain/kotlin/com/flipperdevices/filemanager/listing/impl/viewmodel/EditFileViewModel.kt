package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.FlipperFileNameValidator
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.ByteString
import okio.Path
import okio.buffer
import javax.inject.Inject

class EditFileViewModel @Inject constructor(
    featureProvider: FFeatureProvider,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "CreateFolderViewModel"

    private val channel = Channel<Event>()
    val event = channel.receiveAsFlow()

    private val fileNameValidator = FlipperFileNameValidator()
    private val mutex = Mutex()

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    fun onCreate(path: Path, fileType: FileType) {
        _state.update {
            State.Edit.Create(
                name = "",
                itemType = fileType,
                path = path,
                isValid = false
            )
        }
    }

    fun onRename(pathWithType: PathWithType) {
        _state.update {
            State.Edit.Rename(
                name = pathWithType.fullPath.name,
                itemType = pathWithType.fileType,
                fullPath = pathWithType.fullPath,
                isValid = true
            )
        }
    }

    fun dismiss() {
        _state.value = State.Pending
    }

    fun onNameChange(name: String) {
        val visibleState = state.value as? State.Edit ?: return
        _state.value = visibleState.with(name = name)
    }

    fun onOptionSelected(index: Int) {
        val visibleState = state.value as? State.Edit ?: return
        val option = visibleState.options.getOrNull(index) ?: return
        _state.update { visibleState.with(name = option) }
    }

    private val featureState = featureProvider.get<FStorageFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)

    val canCreateFiles = featureState
        .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private suspend fun uploadFolder(uploadApi: FFileUploadApi, pathOnFlipper: Path) {
        uploadApi.mkdir(pathOnFlipper.toString())
            .onSuccess { channel.send(Event.FilesChanged) }
            .onFailure { error(it) { "Could not create folder" } }
    }

    private suspend fun uploadFile(uploadApi: FFileUploadApi, pathOnFlipper: Path) {
        runCatching {
            uploadApi.sink(
                pathOnFlipper = pathOnFlipper.toString(),
                priority = StorageRequestPriority.FOREGROUND
            ).buffer().use { it.write(ByteString.of()) }
        }.onSuccess { channel.send(Event.FilesChanged) }
            .onFailure { error(it) { "Could not create file" } }
    }

    fun onFinish() {
        val state = state.value as? State.Edit ?: return
        if (!state.isValid) return
        if (!canCreateFiles.value) return
        launchWithLock(mutex, viewModelScope, "create folder") {
            _state.emit(state.with(isLoading = true))
            val storageApi = featureState
                .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
                .first()
                .featureApi

            val uploadApi = storageApi.uploadApi()
            when (state) {
                is State.Edit.Create -> {
                    val pathOnFlipper = state.path.resolve(state.name)
                    when (state.itemType) {
                        FileType.FILE -> uploadFile(
                            uploadApi = uploadApi,
                            pathOnFlipper = pathOnFlipper
                        )

                        FileType.DIR -> uploadFolder(
                            uploadApi = uploadApi,
                            pathOnFlipper = pathOnFlipper
                        )
                    }
                }

                is State.Edit.Rename -> {
                    val pathOnFlipper = state.fullPath.parent?.resolve(state.name) ?: run {
                        error { "#onFinish could not move file because parent is null ${state.fullPath}" }
                        return@launchWithLock
                    }
                    // todo folders doesn't rename
                    uploadApi.move(
                        oldPath = state.fullPath,
                        newPath = pathOnFlipper
                    ).onSuccess { channel.send(Event.FilesChanged) }
                        .onFailure { error(it) { "#onFinish could not move file ${state.fullPath} -> $pathOnFlipper" } }
                }
            }

            _state.emit(State.Pending)
        }
    }

    init {
        state
            .filterIsInstance<State.Edit>()
            .distinctUntilChangedBy { state -> state.name }
            .onEach { state ->
                _state.emit(state.with(isValid = fileNameValidator.isValid(state.name)))
            }.launchIn(viewModelScope)
    }

    sealed interface State {
        data object Pending : State

        sealed interface Edit : State {
            val name: String
            val isValid: Boolean
            val itemType: FileType
            val isLoading: Boolean

            fun with(
                name: String = this.name,
                isValid: Boolean = this.isValid,
                isLoading: Boolean = this.isLoading
            ): Edit

            data class Create(
                val path: Path,
                override val name: String = "",
                override val isValid: Boolean = false,
                override val itemType: FileType,
                override val isLoading: Boolean = false,
            ) : Edit {
                override fun with(name: String, isValid: Boolean, isLoading: Boolean): Create {
                    return copy(
                        name = name,
                        isValid = isValid,
                        isLoading = isLoading
                    )
                }
            }

            data class Rename(
                val fullPath: Path,
                override val name: String = "",
                override val isValid: Boolean = false,
                override val itemType: FileType,
                override val isLoading: Boolean = false
            ) : Edit {
                override fun with(name: String, isValid: Boolean, isLoading: Boolean): Rename {
                    return copy(
                        name = name,
                        isValid = isValid,
                        isLoading = isLoading
                    )
                }
            }

            val options
                get() = when (itemType) {
                    FileType.FILE -> listOf("txt")
                        .plus(FlipperKeyType.entries.map { it.extension })
                        .map { extension -> "$name.$extension" }

                    FileType.DIR -> emptyList()
                }.toImmutableList()

            val needShowOptions
                get() = !name.contains(".") && options.isNotEmpty()
        }
    }

    sealed interface Event {
        data object FilesChanged : Event
    }
}
