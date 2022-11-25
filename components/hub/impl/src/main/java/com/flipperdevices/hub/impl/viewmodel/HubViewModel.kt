package com.flipperdevices.hub.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tangle.viewmodel.VMInject

class HubViewModel @VMInject constructor(
    dataStore: DataStore<Settings>
) : ViewModel() {
    private val fapHubEnabledStateFlow = MutableStateFlow(false)

    init {
        dataStore.data.onEach {
            fapHubEnabledStateFlow.emit(it.applicationCatalog)
        }.launchIn(viewModelScope)
    }

    fun isFapHubEnabled(): StateFlow<Boolean> = fapHubEnabledStateFlow
}
