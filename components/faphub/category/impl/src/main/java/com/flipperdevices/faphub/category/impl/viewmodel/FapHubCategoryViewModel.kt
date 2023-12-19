package com.flipperdevices.faphub.category.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.api.model.SortType.Companion.toSortType
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FapHubCategoryViewModel @AssistedInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    @Assisted private val category: FapCategory,
    fapHubHideItemApi: FapHubHideItemApi,
    targetProviderApi: FlipperTargetProviderApi,
    private val dataStoreSettings: DataStore<Settings>
) : ViewModel() {
    private val sortState by lazy {
        dataStoreSettings.data
            .map { it.selectedCatalogSort.toSortType() }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                initialValue = SortType.UPDATE_AT_DESC
            )
    }

    fun getSortTypeFlow(): StateFlow<SortType> = sortState

    val faps = combine(
        sortState,
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

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Default) {
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
