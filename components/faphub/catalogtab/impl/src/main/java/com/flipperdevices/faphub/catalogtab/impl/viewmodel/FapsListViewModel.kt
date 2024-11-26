package com.flipperdevices.faphub.catalogtab.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flipperdevices.bridge.dao.api.FapHubHideItemApi
import com.flipperdevices.core.pager.distinctBy
import com.flipperdevices.core.pager.loadingPagingDataFlow
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.dao.api.model.SortType.Companion.toSortType
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class FapsListViewModel @Inject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val fapManifestApi: FapManifestApi,
    fapHubHideItemApi: FapHubHideItemApi,
    targetProviderApi: FlipperTargetProviderApi,
    private val dataStoreSettings: DataStore<Settings>
) : DecomposeViewModel() {
    private val sortStateFlow = MutableStateFlow(SortType.UPDATE_AT_DESC)

    init {
        dataStoreSettings
            .data
            .onEach {
                sortStateFlow.emit(it.selected_catalog_sort.toSortType())
            }.launchIn(viewModelScope)
    }

    fun getSortTypeFlow() = sortStateFlow.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
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
            FapsPagingSource(fapNetworkApi, sortType, target, hiddenItems)
        }.flow.distinctBy { it.id }
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    fun getFapsFlow(): Flow<PagingData<FapItemShort>> = faps

    fun onSelectSortType(sortType: SortType) {
        viewModelScope.launch {
            dataStoreSettings.updateData {
                it.copy(
                    selected_catalog_sort = sortType.toSelectedSortType()
                )
            }
        }
    }

    fun refreshManifest() {
        fapManifestApi.invalidateAsync()
    }
}
