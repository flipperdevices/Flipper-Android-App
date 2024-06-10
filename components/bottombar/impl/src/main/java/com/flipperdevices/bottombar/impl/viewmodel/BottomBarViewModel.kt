package com.flipperdevices.bottombar.impl.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.installedtab.api.FapUpdatePendingCountApi
import com.flipperdevices.toolstab.api.ToolsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BottomBarViewModel @Inject constructor(
    toolsApi: ToolsApi,
    fapUpdatePendingCountApi: FapUpdatePendingCountApi
) : DecomposeViewModel() {
    private val hasNotificationToolsStateFlow = MutableStateFlow(false)
    private val hasNotificationAppsStateFlow = MutableStateFlow(false)

    init {
        toolsApi.hasNotification(viewModelScope).onEach {
            hasNotificationToolsStateFlow.emit(it)
        }.launchIn(viewModelScope)
        fapUpdatePendingCountApi.getUpdatePendingCount().onEach {
            hasNotificationAppsStateFlow.emit(it > 0)
        }.launchIn(viewModelScope)
    }

    fun hasNotificationHubState() = hasNotificationToolsStateFlow.asStateFlow()

    fun hasNotificationAppsState() = hasNotificationAppsStateFlow.asStateFlow()
}
