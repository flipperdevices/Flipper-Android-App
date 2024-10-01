package com.flipperdevices.filemanager.listing.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    fun tryDelete(paths: Set<Path>) {
        _state.update { State.Confirm(paths.toImmutableSet()) }
    }

    fun onCancel() {
        _state.update { State.Pending }
    }

    fun onDeleteConfirm() {
        val state = state.value as? State.Confirm ?: return
        launchWithLock(mutex, viewModelScope, "delete") {
            _state.emit(State.Deleting(state.paths))
            val storageApi = featureProvider.getSync<FStorageFeatureApi>()
            if (storageApi == null) {
                channel.send(Event.CouldNotDeleteFile)
                _state.emit(State.Pending)
                return@launchWithLock
            }
            val failureCount = state.paths.map { path ->
                storageApi.deleteApi()
                    .delete(path.toString(), recursive = true)
                    .onSuccess { channel.send(Event.FileDeleted(path)) }
            }.count { it.isFailure }
            if (failureCount > 0) {
                channel.send(Event.CouldNotDeleteFile)
            }
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
        data object CouldNotDeleteFile : Event
    }
}
