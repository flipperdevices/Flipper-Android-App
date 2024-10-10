package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDeleteApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.Path
import javax.inject.Inject

class DeleteFilesViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "DeleteFileViewModel"

    private val channel = Channel<Event>()
    val event = channel.receiveAsFlow()

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    private val mutex = Mutex()

    private val featureState = featureProvider.get<FStorageFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)

    val canDeleteFiles = featureState
        .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun tryDelete(path: Path) {
        tryDelete(listOf(path))
    }

    fun tryDelete(paths: Iterable<Path>) {
        _state.update { State.Confirm(paths.toImmutableSet()) }
    }

    fun onCancel() {
        _state.update { State.Pending }
    }

    private suspend fun deleteFiles(deleteApi: FFileDeleteApi, paths: Set<Path>) {
        val failureCount = paths.map { path ->
            deleteApi
                .delete(path.toString(), recursive = true)
                .onSuccess { channel.send(Event.FileDeleted(path)) }
                .onFailure { error(it) { "Could not delete file $path" } }
        }.count { it.isFailure }
        if (failureCount > 0) {
            channel.send(Event.CouldNotDeleteSomeFiles)
        }
    }

    fun onDeleteConfirm() {
        val state = state.value as? State.Confirm ?: return
        launchWithLock(mutex, viewModelScope, "delete") {
            _state.emit(State.Deleting(state.paths))
            val storageApi = featureState
                .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
                .first()
                .featureApi
            deleteFiles(storageApi.deleteApi(), state.paths)
            _state.emit(State.Pending)
        }
    }

    sealed interface State {
        data object Pending : State
        data class Confirm(val paths: ImmutableSet<Path>) : State
        data class Deleting(val paths: ImmutableSet<Path>) : State

        val fileNamesOrNull: List<String>?
            get() = when (this) {
                is Confirm -> emptyList()
                is Deleting -> paths.map(Path::name)
                Pending -> emptyList()
            }
    }

    sealed interface Event {
        data class FileDeleted(val path: Path) : Event
        data object CouldNotDeleteSomeFiles : Event
    }
}
