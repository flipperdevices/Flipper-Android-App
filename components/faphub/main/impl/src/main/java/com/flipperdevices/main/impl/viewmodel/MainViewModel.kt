package com.flipperdevices.main.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.main.impl.model.FapHubTabEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor() : DecomposeViewModel() {
    private val tabFlow = MutableStateFlow(FapHubTabEnum.APPS)

    fun getTabFlow(): StateFlow<FapHubTabEnum> = tabFlow

    fun onSelectTab(tabEnum: FapHubTabEnum) {
        viewModelScope.launch {
            tabFlow.emit(tabEnum)
        }
    }
}
