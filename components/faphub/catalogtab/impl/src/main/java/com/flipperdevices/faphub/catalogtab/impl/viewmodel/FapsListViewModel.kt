package com.flipperdevices.faphub.catalogtab.impl.viewmodel

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
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.api.model.SortType.Companion.toSortType
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class FapsListViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val fapManifestApi: FapManifestApi,
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
            FapsPagingSource(fapNetworkApi, sortType, target, hiddenItems)
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

    fun refreshManifest() {
        fapManifestApi.invalidateAsync()
    }
}
