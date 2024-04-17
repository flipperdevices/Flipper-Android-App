package com.flipperdevices.bottombar.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.hub.api.HubApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

class BottomBarViewModel @Inject constructor(
    hubApi: HubApi
) : DecomposeViewModel() {
    private val hasNotificationHubStateFlow = MutableStateFlow(false)

    init {
        hubApi.hasNotification(viewModelScope).onEach {
            hasNotificationHubStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun hasNotificationHubState(): StateFlow<Boolean> = hasNotificationHubStateFlow
}
