package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.listing.impl.util.FlipperFileNameValidator
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

class CreateFileViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "CreateFolderViewModel"

    private val channel = Channel<Event>()
    val event = channel.receiveAsFlow()

    private val fileNameValidator = FlipperFileNameValidator()
    private val mutex = Mutex()

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    fun onCreateFileClick() {
        _state.update {
            State.Visible(
                name = "",
                itemType = ItemType.FILE,
            )
        }
    }

    fun onCreateFolderClick() {
        _state.update {
            State.Visible(
                name = "",
                itemType = ItemType.FOLDER,
            )
        }
    }

    fun dismiss() {
        _state.value = State.Pending
    }

    fun onNameChange(name: String) {
        val visibleState = state.value as? State.Visible ?: return
        _state.value = visibleState.copy(name = name)
    }

    fun onOptionSelected(index: Int) {
        val visibleState = state.value as? State.Visible ?: return
        val option = visibleState.options.getOrNull(index) ?: return
        _state.update { visibleState.copy(name = option) }
    }

    private val featureState = featureProvider.get<FStorageFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)

    val canCreateFiles = featureState
        .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private suspend fun uploadFolder(uploadApi: FFileUploadApi, pathOnFlipper: Path) {
        runCatching {
            uploadApi.mkdir(pathOnFlipper.toString())
        }.onSuccess { channel.send(Event.FilesChanged) }
    }

    private suspend fun uploadFile(uploadApi: FFileUploadApi, pathOnFlipper: Path) {
        runCatching {
            uploadApi.sink(
                pathOnFlipper = pathOnFlipper.toString(),
                priority = StorageRequestPriority.FOREGROUND
            ).buffer().use { it.write(ByteString.of()) }
        }.onSuccess { channel.send(Event.FilesChanged) }
    }

    fun onCreate(path: Path) {
        val state = state.value as? State.Visible ?: return
        if (!state.isValid) return
        if (!canCreateFiles.value) return
        launchWithLock(mutex, viewModelScope, "create folder") {
            _state.emit(state.copy(isLoading = true))
            val storageApi = featureState
                .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
                .first()
                .featureApi

            val uploadApi = storageApi.uploadApi()
            val pathOnFlipper = path.resolve(state.name)
            when (state.itemType) {
                ItemType.FILE -> uploadFile(
                    uploadApi = uploadApi,
                    pathOnFlipper = pathOnFlipper
                )

                ItemType.FOLDER -> uploadFolder(
                    uploadApi = uploadApi,
                    pathOnFlipper = pathOnFlipper
                )
            }
            _state.emit(State.Pending)
        }
    }

    init {
        state
            .filterIsInstance<State.Visible>()
            .distinctUntilChangedBy { state -> state.name }
            .onEach { state ->
                _state.emit(state.copy(isValid = fileNameValidator.isValid(state.name)))
            }.launchIn(viewModelScope)
    }

    sealed interface State {
        data object Pending : State

        data class Visible(
            val name: String = "",
            val isValid: Boolean = false,
            val itemType: ItemType,
            val isLoading: Boolean = false
        ) : State {
            val options = when (itemType) {
                ItemType.FILE -> listOf("txt")
                    .plus(FlipperKeyType.entries.map { it.extension })
                    .map { extension -> "$name.$extension" }

                ItemType.FOLDER -> emptyList()
            }.toImmutableList()

            val needShowOptions = !name.contains(".") && options.isNotEmpty()
        }
    }

    sealed interface Event {
        data object FilesChanged : Event
    }

    enum class ItemType {
        FILE,
        FOLDER
    }
}
