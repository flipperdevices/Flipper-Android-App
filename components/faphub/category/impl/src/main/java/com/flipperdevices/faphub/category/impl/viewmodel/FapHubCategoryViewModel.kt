package com.flipperdevices.faphub.category.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.faphub.category.impl.api.CATEGORY_OPEN_PATH_KEY
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class FapHubCategoryViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    @TangleParam(CATEGORY_OPEN_PATH_KEY)
    private val category: FapCategory
) : ViewModel() {
    private val sortTypeFlow = MutableStateFlow(SortType.UPDATED)

    val faps = sortTypeFlow.flatMapLatest { sortType ->
        Pager(PagingConfig(pageSize = 10)) {
            FapsCategoryPagingSource(fapNetworkApi, category, sortType)
        }.flow
    }.cachedIn(viewModelScope)

    fun getSortTypeFlow(): StateFlow<SortType> = sortTypeFlow

    fun getCategoryName() = category.name

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch {
            sortTypeFlow.emit(sortType)
        }
    }
}