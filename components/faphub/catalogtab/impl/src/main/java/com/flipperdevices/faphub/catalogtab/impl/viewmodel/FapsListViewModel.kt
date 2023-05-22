package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapsListViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel() {
    private val sortTypeFlow = MutableStateFlow(SortType.NAME_DESC)
    val faps = sortTypeFlow.flatMapLatest { sortType ->
        Pager(
            PagingConfig(pageSize = FAPS_PAGE_SIZE)
        ) {
            FapsPagingSource(fapNetworkApi, sortType)
        }.flow
    }.cachedIn(viewModelScope)

    fun getSortTypeFlow(): StateFlow<SortType> = sortTypeFlow

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch {
            sortTypeFlow.emit(sortType)
        }
    }
}
