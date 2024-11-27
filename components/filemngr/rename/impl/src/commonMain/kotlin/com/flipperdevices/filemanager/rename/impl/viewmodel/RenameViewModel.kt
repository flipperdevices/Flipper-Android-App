package com.flipperdevices.filemanager.rename.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileUploadApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.core.ktx.jre.FlipperFileNameValidator
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.Path
import javax.inject.Inject

class RenameViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "RenameViewModel"

    private val fileNameValidator = FlipperFileNameValidator()

    private val eventChannel = Channel<Event>()
    val event = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    private var featureJob: Job? = null
    private val featureMutex = Mutex()

    val canRenameFiles = featureProvider.get<FStorageFeatureApi>()
        .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun startRename(fullPath: Path, type: FileType) {
        _state.update {
            State.Renaming(
                fullPath = fullPath,
                type = type,
                name = fullPath.name,
                isValid = false,
                isRenaming = false
            )
        }
    }

    fun dismiss() {
        _state.update { State.Pending }
    }

    fun onNameChange(name: String) {
        _state.update { state ->
            (state as? State.Renaming)
                ?.copy(name = name)
                ?: state
        }
    }

    fun onOptionSelected(index: Int) {
        _state.update { state ->
            (state as? State.Renaming)?.let { renamingState ->
                val option = renamingState.options.getOrNull(index).orEmpty()
                renamingState.copy(name = "${renamingState.name}.$option")
            } ?: state
        }
    }

    private suspend fun rename(uploadApi: FFileUploadApi) {
        val state = (state.first() as? State.Renaming)
        if (state == null) {
            error { "#rename state was not Renaming" }
            return
        }
        val newPath = state.fullPath.parent?.resolve(state.name)
        if (newPath == null) {
            error { "#rename parent path was null for ${state.fullPath}" }
            return
        }
        _state.emit(state.copy(isRenaming = true))
        uploadApi.move(
            oldPath = state.fullPath,
            newPath = newPath
        ).onSuccess {
            val event = Event.Renamed(
                oldFullPath = state.fullPath,
                newFullPath = newPath
            )
            eventChannel.send(event)
        }.onFailure {
            error(it) { "#onFinish could not move file ${state.fullPath} -> $newPath" }
        }
        _state.emit(State.Pending)
        featureJob?.cancelAndJoin()
    }

    fun onConfirm() {
        viewModelScope.launch {
            featureJob?.cancelAndJoin()
            featureMutex.withLock {
                featureJob = featureProvider.get<FStorageFeatureApi>()
                    .onEach { status ->
                        when (status) {
                            FFeatureStatus.NotFound -> Unit
                            FFeatureStatus.Retrieving -> Unit
                            FFeatureStatus.Unsupported -> Unit
                            is FFeatureStatus.Supported -> {
                                rename(status.featureApi.uploadApi())
                            }
                        }
                    }.launchIn(viewModelScope)
                featureJob?.join()
            }
        }
    }

    private fun collectNameValidation() {
        state
            .filterIsInstance<State.Renaming>()
            .distinctUntilChangedBy { state -> state.name }
            .onEach { state ->
                _state.emit(state.copy(isValid = fileNameValidator.isValid(state.name)))
            }.launchIn(viewModelScope)
    }

    init {
        collectNameValidation()
    }

    sealed interface State {
        data object Pending : State
        data class Renaming(
            val fullPath: Path,
            val name: String,
            val isValid: Boolean,
            val type: FileType,
            val isRenaming: Boolean
        ) : State {
            val options: ImmutableList<String>
                get() = when (type) {
                    FileType.FILE -> FileManagerConstants.FILE_EXTENSION_HINTS

                    FileType.DIR -> emptyList()
                }.toImmutableList()

            val needShowOptions: Boolean
                get() = !name.contains(".") && options.isNotEmpty()
        }
    }

    sealed interface Event {
        data class Renamed(val oldFullPath: Path, val newFullPath: Path) : Event
    }
}
