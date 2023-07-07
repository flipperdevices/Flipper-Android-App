package com.flipperdevices.faphub.search.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapHubSearchViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    targetProviderApi: FlipperTargetProviderApi
) : ViewModel() {
    private val searchRequestFlow = MutableStateFlow("")

    val faps = combine(
        searchRequestFlow,
        targetProviderApi.getFlipperTarget()
    ) { searchRequest, target ->
        if (target == null) {
            return@combine loadingPagingDataFlow()
        }
        Pager(PagingConfig(pageSize = FAPS_PAGE_SIZE)) {
            FapsSearchPagingSource(fapNetworkApi, searchRequest, target)
        }.flow
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    fun getSearchRequest() = searchRequestFlow.asStateFlow()

    fun onChangeSearchText(text: String) {
        viewModelScope.launch(Dispatchers.Default) {
            searchRequestFlow.emit(text)
        }
    }
}
