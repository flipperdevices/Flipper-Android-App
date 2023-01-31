package com.flipperdevices.bottombar.impl.main.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class BottomNavigationViewModel @VMInject constructor(
    private val settingsDataStore: DataStore<Settings>
) : ViewModel() {
    private val selectedTabInternal = MutableStateFlow(FlipperBottomTab.DEVICE)

    val selectedTab: StateFlow<FlipperBottomTab>
        get() = selectedTabInternal

    fun onSelectTab(tab: FlipperBottomTab) {
        viewModelScope.launch {
            selectedTabInternal.emit(tab)
            settingsDataStore.updateData {
                it.toBuilder()
                    .setSelectedTab(
                        when (tab) {
                            FlipperBottomTab.DEVICE -> SelectedTab.DEVICE
                            FlipperBottomTab.ARCHIVE -> SelectedTab.ARCHIVE
                            FlipperBottomTab.HUB -> SelectedTab.HUB
                        }
                    ).build()
            }
        }
    }
}
