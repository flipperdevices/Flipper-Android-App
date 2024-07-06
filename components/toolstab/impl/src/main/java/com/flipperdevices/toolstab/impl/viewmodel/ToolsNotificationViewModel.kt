package com.flipperdevices.toolstab.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.toolstab.api.ToolsApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ToolsNotificationViewModel @Inject constructor(
    toolsApi: ToolsApi
) : DecomposeViewModel() {
    val hasNotificationStateFlow: StateFlow<Boolean> = toolsApi
        .hasNotification(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
