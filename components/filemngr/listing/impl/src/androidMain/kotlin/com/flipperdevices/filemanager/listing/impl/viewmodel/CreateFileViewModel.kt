package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.listing.impl.util.FlipperFileNameValidator
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
                currentAction = CreateFileAction.FILE,
            )
        }
    }

    fun onCreateFolderClick() {
        _state.update {
            State.Visible(
                name = "",
                currentAction = CreateFileAction.FOLDER,
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

    fun onCreate(path: Path) {
        val state = state.value as? State.Visible ?: return
        if (!state.isValid) return
        launchWithLock(mutex, viewModelScope, "create folder") {
            _state.emit(state.copy(isLoading = true))
            val storageApi = featureProvider
                .getSync<FStorageFeatureApi>()
                ?: run {
                    _state.emit(State.Pending)
                    return@launchWithLock
                }
            val uploadApi = storageApi.uploadApi()
            val pathOnFlipper = path.resolve(state.name).toString()
            when (state.currentAction) {
                CreateFileAction.FILE -> runCatching {
                    uploadApi.sink(
                        pathOnFlipper = pathOnFlipper,
                        priority = StorageRequestPriority.FOREGROUND
                    ).buffer().use { it.write(ByteString.of()) }
                }.onSuccess { channel.send(Event.FilesChanged) }

                CreateFileAction.FOLDER -> runCatching {
                    uploadApi.mkdir(pathOnFlipper)
                }.onSuccess { channel.send(Event.FilesChanged) }
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
            val currentAction: CreateFileAction,
            val isLoading: Boolean = false
        ) : State {
            val options = when (currentAction) {
                CreateFileAction.FILE -> listOf("txt")
                    .plus(FlipperKeyType.entries.map { it.extension })
                    .map { extension -> "$name.$extension" }

                CreateFileAction.FOLDER -> emptyList()
            }.toImmutableList()
            val needShowOptions = !name.contains(".") && options.isNotEmpty()
        }
    }

    sealed interface Event {
        data object FilesChanged : Event
    }

    enum class CreateFileAction {
        FILE,
        FOLDER
    }
}
