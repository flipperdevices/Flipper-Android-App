package com.flipperdevices.archive.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.archive.search.di.SearchComponent
import com.flipperdevices.archive.search.model.SearchState
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val queryFlow = MutableStateFlow("")
    private val searchState = MutableStateFlow<SearchState>(SearchState.Loading)

    @Inject
    lateinit var utilsKeyApi: UtilsKeyApi

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var keyScreenApi: KeyScreenApi

    init {
        ComponentHolder.component<SearchComponent>().inject(this)
        queryFlow.mapLatest { query ->
            searchState.emit(SearchState.Loading)
            utilsKeyApi.search(query)
                .map { keys -> keys.map { keyParser.parseKey(it) to it.path } }
                .collect {
                    searchState.emit(SearchState.Loaded(it))
                }
        }.launchIn(viewModelScope)
    }

    fun getState(): StateFlow<SearchState> = searchState

    fun onChangeText(text: String) {
        viewModelScope.launch {
            queryFlow.emit(text)
        }
    }

    fun openKeyScreen(router: Router, keyPath: FlipperKeyPath) {
        router.navigateTo(keyScreenApi.getKeyScreenScreen(keyPath))
    }
}
