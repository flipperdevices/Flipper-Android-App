package com.flipperdevices.faphub.installedtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class InstalledFapsViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel(), LogTagProvider {
    override val TAG = "InstalledFapsViewModel"
    private val fapInstalledScreenStateFlow = MutableStateFlow<FapInstalledScreenState>(
        FapInstalledScreenState.Loading
    )

    init {
        onRefresh()
    }

    fun getFapInstalledScreenState(): StateFlow<FapInstalledScreenState> =
        fapInstalledScreenStateFlow

    fun onRefresh() = viewModelScope.launch {
        fapNetworkApi.getAllItem(sortType = SortType.UPDATE_AT_DESC, offset = 0, limit = 4)
            .onSuccess { faps ->
                fapInstalledScreenStateFlow.emit(
                    FapInstalledScreenState.Loaded(
                        faps.toImmutableList()
                    )
                )
            }.onFailure {
                error(it) { "Failed get installed fap" }
                fapInstalledScreenStateFlow.emit(FapInstalledScreenState.Error(it))
            }
    }
}
