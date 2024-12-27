package com.flipperdevices.bridge.connection.screens.device.viewmodel

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FCurrentDeviceViewModel @Inject constructor(
    private val orchestrator: FDeviceOrchestrator,
    persistedStorage: FDevicePersistedStorage
) : DecomposeViewModel() {

    init {
        persistedStorage.getCurrentDevice()
            .onEach { device ->
                if (device == null) {
                    orchestrator.disconnectCurrent()
                } else {
                    orchestrator.connect(device)
                }
            }.launchIn(viewModelScope)
    }

    fun getState() = orchestrator.getState()
}
