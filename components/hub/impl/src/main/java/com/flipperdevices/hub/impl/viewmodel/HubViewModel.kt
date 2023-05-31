package com.flipperdevices.hub.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tangle.viewmodel.VMInject

class HubViewModel @VMInject constructor(
    private val preference: DataStore<Settings>
) : ViewModel() {
    private val isApplicationCatalogEnabledFlow = MutableStateFlow(false)

    init {
        preference.data.onEach {
            isApplicationCatalogEnabledFlow.emit(it.applicationCatalog)
        }.launchIn(viewModelScope)
    }

    fun isApplicationCatalogEnabledFlow() = isApplicationCatalogEnabledFlow.asStateFlow()
}
