package com.flipperdevices.faphub.installedtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class InstalledFapsViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi
) : ViewModel() {
    private val fapInstalledScreenStateFlow = MutableStateFlow<FapInstalledScreenState>(
        FapInstalledScreenState.Loading
    )

    init {
        viewModelScope.launch {
            val faps = fapNetworkApi.getAllItem(sortType = SortType.UPDATED).take(n = 4)
            fapInstalledScreenStateFlow.emit(FapInstalledScreenState.Loaded(faps))
        }
    }

    fun getFapInstalledScreenState(): StateFlow<FapInstalledScreenState> =
        fapInstalledScreenStateFlow
}
