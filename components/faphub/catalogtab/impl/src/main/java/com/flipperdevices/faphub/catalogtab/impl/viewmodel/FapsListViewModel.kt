package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapsListViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val fapManifestApi: FapManifestApi,
    targetProviderApi: FlipperTargetProviderApi
) : ViewModel() {
    private val sortTypeFlow = MutableStateFlow(SortType.NAME_DESC)
    val faps = combine(sortTypeFlow, targetProviderApi.getFlipperTarget()) { sortType, target ->
        if (target == null) {
            return@combine loadingPagingDataFlow()
        }
        return@combine Pager(
            PagingConfig(pageSize = FAPS_PAGE_SIZE)
        ) {
            FapsPagingSource(fapNetworkApi, sortType, target)
        }.flow
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    fun getSortTypeFlow(): StateFlow<SortType> = sortTypeFlow

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Default) {
            sortTypeFlow.emit(sortType)
        }
    }

    fun refreshManifest() {
        fapManifestApi.invalidateAsync()
    }
}
