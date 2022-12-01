package com.flipperdevices.main.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.main.impl.model.FapHubTabEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class MainViewModel @VMInject constructor(
    settings: DataStore<Settings>
) : ViewModel() {
    private val tabFlow = MutableStateFlow(FapHubTabEnum.APPS)
    private val experimentalSwitchFlow = MutableStateFlow(false)

    init {
        settings.data.onEach {
            experimentalSwitchFlow.emit(it.faphubNewSwitch)
        }.launchIn(viewModelScope)
    }

    fun getTabFlow(): StateFlow<FapHubTabEnum> = tabFlow

    fun isExperimentalSwitch(): StateFlow<Boolean> = experimentalSwitchFlow

    fun onSelectTab(tabEnum: FapHubTabEnum) {
        viewModelScope.launch {
            tabFlow.emit(tabEnum)
        }
    }
}
