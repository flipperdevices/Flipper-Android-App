package com.flipperdevices.filemanager.listing.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.FileManagerSort
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.listing.impl.model.ExtendedListingItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.sync.Mutex
import okio.Path
import okio.Path.Companion.toPath

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
                                    !it.path.name.startsWith(".")
                                }
                            }
                            .sortedByDescending {
                                when (settings.file_manager_sort) {
                                    is FileManagerSort.Unrecognized,
                                    FileManagerSort.DEFAULT -> null

                                    FileManagerSort.SIZE -> {
                                        when (it) {
                                            is ExtendedListingItem.File -> it.size
                                            // The default size for folder is 0
                                            // Here's placed 0 so sort works as on flipper
                                            is ExtendedListingItem.Folder -> 0
                                        }
                                    }
                                }
                            }
                            .toImmutableList()
                    )
                }
            }
        }
    ).stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading)

    private suspend fun updateFiles(
        items: List<ExtendedListingItem>,
        listingApi: FListingStorageApi
    ) {
        items
            .filterIsInstance<ExtendedListingItem.Folder>()
            .filter { directory -> directory.itemsCount == null }
            .onEach { directory ->
                _state.update { state ->
                    val loadedState = (state as? State.Loaded)
                    if (loadedState == null) {
                        error { "#updateFiles state changed during update" }
                        return@update state
                    }
                    val newList = loadedState.files.toMutableList()
                    val i = newList.indexOfFirst { item -> item == directory }
                    if (i == -1) {
                        error { "#updateFiles could not find item in list" }
                        return@update loadedState
                    }
                    val itemsCount = listingApi.ls(path.resolve(directory.path).toString())
                        .getOrNull()
                        .orEmpty()
                        .size
                    val updatedDirectory = directory.copy(itemsCount = itemsCount)
                    newList[i] = updatedDirectory
                    loadedState.copy(files = newList.toImmutableList())
                }
            }
    }

    private fun ListingItem.toExtended(): ExtendedListingItem {
        return when (fileType) {
            FileType.DIR -> {
                ExtendedListingItem.Folder(
                    path = fileName.toPath(),
                    itemsCount = null
                )
            }

            null, FileType.FILE -> {
                ExtendedListingItem.File(
                    path = fileName.toPath(),
                    size = size
                )
            }
        }
    }

    private suspend fun listFiles(listingApi: FListingStorageApi) {
        listingApi.lsFlow(path.toString())
            .toThrowableFlow()
            .catch { _state.emit(State.CouldNotListPath) }
            .map { items -> items.map { item -> item.toExtended() } }
            .onEach { files ->
                _state.updateAndGet { state ->
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
                .filter { it.path.name != path.name }
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

    fun onFilesChanged(items: List<ListingItem>) {
        _state.update { state ->
            (state as? State.Loaded)?.let { loadedState ->
                val newItemsNames = items.map(ListingItem::fileName)
                val newFiles = loadedState.files
                    .filter { item -> !newItemsNames.contains(item.itemName) }
                    .plus(items.map { item -> item.toExtended() })
                    .toImmutableList()
                loadedState.copy(files = newFiles)
            } ?: state
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
        combine(
            flow = featureProvider
                .get<FStorageFeatureApi>()
                .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>(),
            flow2 = state
                .filterIsInstance<State.Loaded>()
                .distinctUntilChangedBy { it.files.size },
            transform = { feature, state ->
                updateFiles(
                    items = state.files,
                    listingApi = feature.featureApi.listingApi()
                )
            }
        ).launchIn(viewModelScope)
    }

    sealed interface State {
        data object Loading : State
        data object Unsupported : State
        data object CouldNotListPath : State
        data class Loaded(
            val files: ImmutableList<ExtendedListingItem>,
        ) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            path: Path
        ): FilesViewModel
    }
}
