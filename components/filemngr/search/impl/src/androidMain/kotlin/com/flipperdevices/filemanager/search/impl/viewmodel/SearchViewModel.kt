package com.flipperdevices.filemanager.search.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.core.ktx.jre.debounceAfterFirst
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    fun onQueryChange(query: String) {
        _searchState.update { it.copy(query = query) }
    }

    private suspend fun itemsFlow(
        listingApi: FListingStorageApi,
        path: Path
    ): Flow<List<SearchItem>> = listingApi
        .lsFlow(path.toString())
        .mapNotNull { items -> items.getOrNull() }
        .map { items ->
            items.map { item ->
                SearchItem(
                    instance = item,
                    fullPath = path.resolve(item.fileName)
                )
            }
        }

    private fun itemsFlowRecursive(
        listingApi: FListingStorageApi,
        path: Path
    ): Flow<List<SearchItem>> = flow {
        itemsFlow(listingApi, path)
            .onEach { searchItems -> emit(searchItems) }
            .onEach { searchItems ->
                searchItems
                    .filter { searchItem -> searchItem.instance.fileType == FileType.DIR }
                    .forEach { searchItem ->
                        itemsFlowRecursive(listingApi, searchItem.fullPath)
                            .onEach { searchItems -> emit(searchItems) }
                            .collect()
                    }
            }.collect()
    }

    private fun trySearch(listingApi: FListingStorageApi) {
        viewModelScope.launch {
            lastSearchJob?.cancelAndJoin()
            launchWithLock(mutex, viewModelScope, "search") {
                lastSearchJob = coroutineContext.job
                val query = queryState.value.query
                _state.emit(State.Loaded(path = path, isSearching = true))
                val itemsFlow = when {
                    query.isEmpty() -> itemsFlow(listingApi, path)
                    else -> itemsFlowRecursive(listingApi, path)
                }
                itemsFlow.onEach {
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
        combine(
            flow = featureState,
            flow2 = _searchState.debounceAfterFirst(timeout = 1000.milliseconds),
            transform = { featureState, _ ->
                when (featureState) {
                    FFeatureStatus.NotFound -> _state.emit(State.Unsupported)
                    FFeatureStatus.Retrieving -> _state.emit(State.Loading)
                    FFeatureStatus.Unsupported -> _state.emit(State.Unsupported)
                    is FFeatureStatus.Supported -> trySearch(featureState.featureApi.listingApi())
                }
            }
        ).launchIn(viewModelScope)
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
