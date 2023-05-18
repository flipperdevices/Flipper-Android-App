package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.catalogtab.impl.model.CategoriesLoadState
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class CategoriesViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel(), LogTagProvider {
    override val TAG = "CategoriesViewModel"
    private val categoriesLoadStateFlow = MutableStateFlow<CategoriesLoadState>(
        CategoriesLoadState.Loading
    )

    init {
        onRefresh()
    }

    fun getCategoriesLoadState(): StateFlow<CategoriesLoadState> = categoriesLoadStateFlow

    fun onRefresh() = viewModelScope.launch {
        fapNetworkApi.getCategories().onSuccess { categories ->
            categoriesLoadStateFlow.emit(CategoriesLoadState.Loaded(categories.toImmutableList()))
        }.onFailure {
            error(it) { "Failed get categories" }
            categoriesLoadStateFlow.emit(CategoriesLoadState.Error(it))
        }
    }
}
