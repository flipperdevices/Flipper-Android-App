package com.flipperdevices.toolstab.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.toolstab.api.ToolsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ToolsNotificationViewModel @Inject constructor(
    toolsApi: ToolsApi
) : DecomposeViewModel() {
    val hasNotificationStateFlow: StateFlow<Boolean> = toolsApi
        .hasNotification(viewModelScope)
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
