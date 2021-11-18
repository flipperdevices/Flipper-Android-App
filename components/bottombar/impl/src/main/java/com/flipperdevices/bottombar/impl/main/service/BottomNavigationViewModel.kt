package com.flipperdevices.bottombar.impl.main.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BottomNavigationViewModel : ViewModel() {
    private val selectedTabInternal = MutableStateFlow(FlipperBottomTab.DEVICE)

    val selectedTab: StateFlow<FlipperBottomTab>
        get() = selectedTabInternal

    fun onSelectTab(tab: FlipperBottomTab) {
        viewModelScope.launch {
            selectedTabInternal.emit(tab)
        }
    }
}
