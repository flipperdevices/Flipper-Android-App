package com.flipperdevices.filemanager.search.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import okio.Path
import kotlin.time.Duration.Companion.milliseconds

class SearchViewModel @AssistedInject constructor(
    featureProvider: FFeatureProvider,
    @Assisted private val path: Path
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "SearchViewModel"
    private val mutex = Mutex()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val queryState = _searchState.asStateFlow()

    private var lastSearchJob: Job? = null

    private val featureState = featureProvider.get<FStorageFeatureApi>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FFeatureStatus.Retrieving)

    private suspend fun listingApiOrNull() = featureState
        .filterIsInstance<FFeatureStatus.Supported<FStorageFeatureApi>>()
        .firstOrNull()
        ?.featureApi
        ?.listingApi()

    fun onQueryChange(query: String) {
        _searchState.updateAndGet { it.copy(query = query) }
    }

    private suspend fun itemsFlow(path: Path): Flow<List<SearchItem>> {
        return listingApiOrNull()
            ?.lsFlow(path.toString())
            ?.mapNotNull { items -> items.getOrNull() }
            ?.map { items ->
                items.map { item ->
                    SearchItem(
                        instance = item,
                        fullPath = path.resolve(item.fileName)
                    )
                }
            } ?: emptyFlow()
    }

    private fun itemsFlowRecursive(
        path: Path
    ): Flow<List<SearchItem>> = flow {
        itemsFlow(path)
            .onEach { searchItems -> emit(searchItems) }
            .onEach { searchItems ->
                searchItems
                    .filter { searchItem -> searchItem.instance.fileType == FileType.DIR }
                    .forEach { searchItem ->
                        itemsFlowRecursive(searchItem.fullPath)
                            .onEach { searchItems -> emit(searchItems) }
                            .collect()
                    }
            }.collect()
    }

    private fun trySearch() {
        val query = queryState.value.query
        viewModelScope.launch {
            lastSearchJob?.cancelAndJoin()
            launchWithLock(mutex, viewModelScope, "search") {
                lastSearchJob = coroutineContext.job
                _state.emit(State.Loading)
                itemsFlowRecursive(path)
                    .onEach {
                        val filteredItems = it.filter { item ->
                            if (query.isEmpty()) {
                                true
                            } else {
                                item.fullPath.name.contains(query, ignoreCase = true)
                            }
                        }
                        _state.update { oldState ->
                            val state = (oldState as? State.Loaded) ?: State.Loaded(
                                isSearching = true,
                                path = path
                            )
                            state.copy(items = state.items.plus(filteredItems).toImmutableList())
                        }
                    }.collect()
                _state.update { oldState ->
                    val state = (oldState as? State.Loaded) ?: State.Loaded(path = path)
                    state.copy(isSearching = false)
                }
            }
        }
    }

    init {
        featureState
            .onEach { status ->
                when (status) {
                    FFeatureStatus.NotFound -> _state.emit(State.Unsupported)
                    FFeatureStatus.Retrieving -> _state.emit(State.Loading)
                    FFeatureStatus.Unsupported -> _state.emit(State.Unsupported)
                    is FFeatureStatus.Supported -> trySearch()
                }
            }.launchIn(viewModelScope)
        // drop initial element
        _searchState
            .drop(count = 1)
            .debounce(timeout = 400.milliseconds)
            .onEach { trySearch() }
            .launchIn(viewModelScope)
    }

    data class SearchState(
        val query: String = ""
    )

    sealed interface State {
        data object Loading : State
        data object Unsupported : State
        data class Loaded(
            val items: ImmutableList<SearchItem> = persistentListOf(),
            val isSearching: Boolean = false,
            val path: Path
        ) : State
    }

    data class SearchItem(
        val instance: ListingItem,
        val fullPath: Path
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            path: Path
        ): SearchViewModel
    }
}
