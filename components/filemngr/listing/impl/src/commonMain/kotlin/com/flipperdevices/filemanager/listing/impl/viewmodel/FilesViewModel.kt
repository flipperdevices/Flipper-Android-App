package com.flipperdevices.filemanager.listing.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.preference.pb.FileManagerSort
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import okio.Path

class FilesViewModel @AssistedInject constructor(
    private val featureProvider: FFeatureProvider,
    @Assisted private val path: Path,
    private val settingsDataStore: DataStore<Settings>
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FilesViewModel"

    private val mutex = Mutex()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = combine(
        flow = settingsDataStore.data,
        flow2 = _state,
        transform = { settings, state ->
            when (state) {
                State.CouldNotListPath -> state
                State.Loading -> state
                State.Unsupported -> state
                is State.Loaded -> {
                    state.copy(
                        files = state.files
                            .filter {
                                if (settings.show_hidden_files_on_flipper) {
                                    true
                                } else {
                                    !it.fileName.startsWith(".")
                                }
                            }
                            .sortedByDescending {
                                when (settings.file_manager_sort) {
                                    is FileManagerSort.Unrecognized,
                                    FileManagerSort.DEFAULT -> null

                                    FileManagerSort.SIZE -> it.size
                                }
                            }
                            .toImmutableList()
                    )
                }
            }
        }
    ).stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading)

    private suspend fun listFiles(listingApi: FListingStorageApi) {
        listingApi.lsFlow(path.toString())
            .toThrowableFlow()
            .catch { _state.emit(State.CouldNotListPath) }
            .onEach { files ->
                _state.update { state ->
                    when (state) {
                        is State.Loaded -> {
                            state.copy(state.files.plus(files).toImmutableList())
                        }

                        else -> {
                            State.Loaded(files.toImmutableList())
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun fileDeleted(path: Path) {
        val loadedState = _state.value as? State.Loaded ?: return
        _state.update {
            val newFileList = loadedState.files
                .filter { it.fileName != path.name }
                .toImmutableList()
            loadedState.copy(files = newFileList)
        }
    }

    fun tryListFiles() {
        launchWithLock(mutex, viewModelScope, "try_list_files") {
            _state.emit(State.Loading)
            val featureApi = featureProvider.getSync<FStorageFeatureApi>()
            if (featureApi == null) {
                _state.emit(State.Unsupported)
                return@launchWithLock
            }
            listFiles(featureApi.listingApi())
        }
    }

    private suspend fun invalidate(
        featureStatus: FFeatureStatus<FStorageFeatureApi>
    ) = withLock(mutex, "invalidate") {
        when (featureStatus) {
            FFeatureStatus.Unsupported,
            FFeatureStatus.NotFound -> {
                _state.emit(State.Unsupported)
            }

            is FFeatureStatus.Supported -> {
                listFiles(featureStatus.featureApi.listingApi())
            }

            FFeatureStatus.Retrieving -> {
                _state.emit(State.Loading)
            }
        }
    }

    init {
        featureProvider
            .get<FStorageFeatureApi>()
            .onEach { featureStatus -> invalidate(featureStatus) }
            .launchIn(viewModelScope)
    }

    sealed interface State {
        data object Loading : State
        data object Unsupported : State
        data object CouldNotListPath : State
        data class Loaded(
            val files: ImmutableList<ListingItem>,
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            path: Path
        ): FilesViewModel
    }
}
