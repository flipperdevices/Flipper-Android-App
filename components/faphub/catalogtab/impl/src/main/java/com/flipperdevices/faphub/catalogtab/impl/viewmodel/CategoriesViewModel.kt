package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.faphub.catalogtab.impl.model.CategoriesLoadState
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class CategoriesViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel() {
    private val categoriesLoadStateFlow = MutableStateFlow<CategoriesLoadState>(
        CategoriesLoadState.Loading
    )

    init {
        viewModelScope.launch {
            val categories = fapNetworkApi.getCategories()
            categoriesLoadStateFlow.emit(CategoriesLoadState.Loaded(categories))
        }
    }

    fun getCategoriesLoadState(): StateFlow<CategoriesLoadState> = categoriesLoadStateFlow
}