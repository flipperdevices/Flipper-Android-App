package com.flipperdevices.faphub.category.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.faphub.category.impl.api.CATEGORY_OPEN_PATH_KEY
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class FapHubCategoryViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    @TangleParam(CATEGORY_OPEN_PATH_KEY)
    private val category: FapCategory,
    targetProviderApi: FlipperTargetProviderApi
) : ViewModel() {
    private val sortTypeFlow = MutableStateFlow(SortType.UPDATE_AT_DESC)

    val faps = combine(sortTypeFlow, targetProviderApi.getFlipperTarget()) { sortType, target ->
        if (target == null) {
            return@combine loadingPagingDataFlow()
        }
        return@combine Pager(
            PagingConfig(pageSize = FAPS_PAGE_SIZE)
        ) {
            FapsCategoryPagingSource(fapNetworkApi, category, sortType, target)
        }.flow
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    fun getSortTypeFlow(): StateFlow<SortType> = sortTypeFlow

    fun getCategoryName() = category.name

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Default) {
            sortTypeFlow.emit(sortType)
        }
    }
}
