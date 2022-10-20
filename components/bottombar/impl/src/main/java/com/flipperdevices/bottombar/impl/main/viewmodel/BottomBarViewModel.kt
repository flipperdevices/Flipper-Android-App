package com.flipperdevices.bottombar.impl.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.hub.api.HubApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tangle.viewmodel.VMInject

class BottomBarViewModel @VMInject constructor(
    hubApi: HubApi
) : ViewModel() {
    private val hasNotificationHubStateFlow = MutableStateFlow(false)

    init {
        hubApi.hasNotification().onEach {
            hasNotificationHubStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun hasNotificationHubState(): StateFlow<Boolean> = hasNotificationHubStateFlow
}
