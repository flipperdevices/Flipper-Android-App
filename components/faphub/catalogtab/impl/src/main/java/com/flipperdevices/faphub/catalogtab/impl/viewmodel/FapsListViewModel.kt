package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItem
import kotlinx.coroutines.flow.Flow
import tangle.viewmodel.VMInject

class FapsListViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel() {
    val faps: Flow<PagingData<FapItem>> = Pager(PagingConfig(pageSize = 10)) {
        FapsPagingSource(fapNetworkApi)
    }.flow.cachedIn(viewModelScope)
}
