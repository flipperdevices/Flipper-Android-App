package com.flipperdevices.main.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.main.impl.model.FapHubTabEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class MainViewModel @VMInject constructor(
    private val settings: DataStore<Settings>,
) : ViewModel() {
    private val tabFlow = MutableStateFlow(FapHubTabEnum.APPS)

    fun getTabFlow(): StateFlow<FapHubTabEnum> = tabFlow

    fun onSelectTab(tabEnum: FapHubTabEnum) {
        viewModelScope.launch {
            tabFlow.emit(tabEnum)
        }
    }
}