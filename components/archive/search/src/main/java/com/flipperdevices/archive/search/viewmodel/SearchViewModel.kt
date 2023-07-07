package com.flipperdevices.archive.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.search.model.SearchState
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.keyparser.api.KeyParser
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import tangle.viewmodel.VMInject

class SearchViewModel @VMInject constructor(
    private val utilsKeyApi: UtilsKeyApi,
    private val keyParser: KeyParser,
    private val synchronizationApi: SynchronizationApi
) : ViewModel() {
    private val queryFlow = MutableStateFlow("")
    private val searchState = MutableStateFlow<SearchState>(SearchState.Loading)
    init {
        queryFlow.mapLatest { query ->
            searchState.emit(SearchState.Loading)
            utilsKeyApi.search(query)
                .map { keys -> keys.map { keyParser.parseKey(it) to it } }
                .collect {
                    searchState.emit(SearchState.Loaded(it.toImmutableList()))
                }
        }.launchIn(viewModelScope + Dispatchers.Default)
    }

    fun getState(): StateFlow<SearchState> = searchState

    fun getSynchronizationState(): StateFlow<SynchronizationState> =
        synchronizationApi.getSynchronizationState()

    fun onChangeText(text: String) {
        viewModelScope.launch(Dispatchers.Default) {
            queryFlow.emit(text)
        }
    }
}
