package com.flipperdevices.faphub.category.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.api.model.SortType.Companion.toSortType
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FapHubCategoryViewModel @AssistedInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    @Assisted private val category: FapCategory,
    fapHubHideItemApi: FapHubHideItemApi,
    targetProviderApi: FlipperTargetProviderApi,
    private val dataStoreSettings: DataStore<Settings>
) : DecomposeViewModel() {
    private val sortStateFlow = MutableStateFlow(SortType.UPDATE_AT_DESC)

    init {
        dataStoreSettings.data
            .onEach {
                sortStateFlow.emit(it.selectedCatalogSort.toSortType())
            }.launchIn(viewModelScope)
    }

    fun getSortTypeFlow() = sortStateFlow.asStateFlow()

    private val faps = combine(
        sortStateFlow,
        targetProviderApi.getFlipperTarget(),
        fapHubHideItemApi.getHiddenItems()
    ) { sortType, target, hiddenItems ->
        if (target == null) {
            return@combine loadingPagingDataFlow()
        }
        return@combine Pager(
            PagingConfig(pageSize = FAPS_PAGE_SIZE)
        ) {
            FapsCategoryPagingSource(fapNetworkApi, category, sortType, target, hiddenItems)
        }.flow
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    fun getFapsFlow() = faps

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setSelectedCatalogSort(sortType.toSelectedSortType())
                    .build()
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            category: FapCategory
        ): FapHubCategoryViewModel
    }
}
