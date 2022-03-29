package com.flipperdevices.bottombar.impl.main.service

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bottombar.impl.di.BottomBarComponent
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.SelectedTab
import com.flipperdevices.core.preference.pb.Settings
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BottomNavigationViewModel : ViewModel() {
    private val selectedTabInternal = MutableStateFlow(FlipperBottomTab.DEVICE)

    @Inject
    lateinit var settingsDataStore: DataStore<Settings>

    init {
        ComponentHolder.component<BottomBarComponent>().inject(this)
    }

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
                            FlipperBottomTab.OPTIONS -> SelectedTab.OPTIONS
                        }
                    ).build()
            }
        }
    }
}
