package com.flipperdevices.bridge.connection.screens.device.viewmodel

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DevicesDropdownState(
    val currentDevice: FDeviceBaseModel?,
    val devices: ImmutableList<FDeviceBaseModel>
)

class FDevicesViewModel @Inject constructor(
    private val devicePersistedStorage: FDevicePersistedStorage
) : DecomposeViewModel() {
    private val devicesState = MutableStateFlow(
        DevicesDropdownState(
            currentDevice = null,
            devices = persistentListOf()
        )
    )

    init {
        combine(
            devicePersistedStorage.getAllDevices(),
            devicePersistedStorage.getCurrentDevice()
        ) { devices, currentDevice ->
            DevicesDropdownState(
                currentDevice = currentDevice,
                devices = devices.toImmutableList()
            )
        }.onEach { devicesState.emit(it) }
            .launchIn(viewModelScope)
    }

    fun getState() = devicesState.asStateFlow()

    fun onSelectDevice(device: FDeviceBaseModel) {
        viewModelScope.launch {
            devicePersistedStorage.setCurrentDevice(device.uniqueId)
        }
    }

    fun onDisconnect() {
        viewModelScope.launch {
            devicePersistedStorage.setCurrentDevice(null)
        }
    }
}
