package com.flipperdevices.faphub.search.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapHubSearchViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel() {
    private val searchRequestFlow = MutableStateFlow("")

    val faps = searchRequestFlow.flatMapLatest { sortType ->
        Pager(PagingConfig(pageSize = 10)) {
            FapsSearchPagingSource(fapNetworkApi, sortType)
        }.flow
    }.cachedIn(viewModelScope)

    fun onChangeSearchText(text: String) {
        viewModelScope.launch {
            searchRequestFlow.emit(text)
        }
    }
}