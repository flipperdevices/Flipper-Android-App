package com.flipperdevices.faphub.category.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.core.preference.pb.SelectedCatalogSort
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.faphub.category.impl.api.CATEGORY_OPEN_PATH_KEY
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class FapHubCategoryViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    @TangleParam(CATEGORY_OPEN_PATH_KEY)
    private val category: FapCategory,
    fapHubHideItemApi: FapHubHideItemApi,
    targetProviderApi: FlipperTargetProviderApi,
    private val dataStoreSettings: DataStore<Settings>
) : ViewModel() {
    private val sortTypeFlow = MutableStateFlow(SortType.UPDATE_AT_DESC)
    fun getSortTypeFlow(): StateFlow<SortType> = sortTypeFlow.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreSettings.data.collectLatest {
                sortTypeFlow.emit(it.selectedCatalogSort.toSortType())
            }
        }
    }

    val faps = combine(
        sortTypeFlow,
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

    fun getCategoryName() = category.name

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Default) {
            dataStoreSettings.updateData {
                it.toBuilder()
                    .setSelectedCatalogSort(sortType.toSelectedSortType())
                    .build()
            }
        }
    }
}

private fun SelectedCatalogSort.toSortType(): SortType = when (this) {
    SelectedCatalogSort.UPDATE_AT_DESC -> SortType.UPDATE_AT_DESC
    SelectedCatalogSort.UPDATE_AT_ASC -> SortType.UPDATE_AT_ASC
    SelectedCatalogSort.CREATED_AT_DESC -> SortType.CREATED_AT_DESC
    SelectedCatalogSort.CREATED_AT_ASC -> SortType.CREATED_AT_ASC
    SelectedCatalogSort.NAME_DESC -> SortType.NAME_DESC
    SelectedCatalogSort.NAME_ASC -> SortType.NAME_ASC
    SelectedCatalogSort.UNRECOGNIZED -> SortType.UPDATE_AT_DESC
}

private fun SortType.toSelectedSortType(): SelectedCatalogSort = when (this) {
    SortType.UPDATE_AT_DESC -> SelectedCatalogSort.UPDATE_AT_DESC
    SortType.UPDATE_AT_ASC -> SelectedCatalogSort.UPDATE_AT_ASC
    SortType.CREATED_AT_DESC -> SelectedCatalogSort.CREATED_AT_DESC
    SortType.CREATED_AT_ASC -> SelectedCatalogSort.CREATED_AT_ASC
    SortType.NAME_DESC -> SelectedCatalogSort.NAME_DESC
    SortType.NAME_ASC -> SelectedCatalogSort.NAME_ASC
}
