package com.flipperdevices.bottombar.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.toolstab.api.ToolsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BottomBarViewModel @Inject constructor(
    toolsApi: ToolsApi
) : DecomposeViewModel() {
    private val hasNotificationHubStateFlow = MutableStateFlow(false)

    init {
        toolsApi.hasNotification(viewModelScope).onEach {
            hasNotificationHubStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun hasNotificationHubState(): StateFlow<Boolean> = hasNotificationHubStateFlow
}
