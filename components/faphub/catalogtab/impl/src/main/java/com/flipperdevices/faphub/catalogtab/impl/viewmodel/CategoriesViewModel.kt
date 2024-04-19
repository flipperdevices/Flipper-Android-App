package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.catalogtab.impl.model.CategoriesLoadState
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val targetProviderApi: FlipperTargetProviderApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "CategoriesViewModel"
    private val categoriesLoadStateFlow = MutableStateFlow<CategoriesLoadState>(
        CategoriesLoadState.Loading
    )
    private val mutex = Mutex()
    private var refreshJob: Job? = null

    init {
        onRefresh()
    }

    fun getCategoriesLoadState(): StateFlow<CategoriesLoadState> = categoriesLoadStateFlow

    fun onRefresh() = launchWithLock(mutex, viewModelScope, "refresh") {
        refreshJob?.cancelAndJoin()
        refreshJob = viewModelScope.launch {
            targetProviderApi.getFlipperTarget().collectLatest { target ->
                if (target == null) {
                    categoriesLoadStateFlow.emit(CategoriesLoadState.Loading)
                    return@collectLatest
                }
                fapNetworkApi.getCategories(target).onSuccess { categories ->
                    categoriesLoadStateFlow.emit(CategoriesLoadState.Loaded(categories.toImmutableList()))
                }.onFailure {
                    error(it) { "Failed get categories" }
                    categoriesLoadStateFlow.emit(CategoriesLoadState.Error(it))
                }
            }
        }
    }
}
