package com.flipperdevices.bridge.connection.screens.search

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class ConnectionSearchViewModel(
    private val persistedStorage: FDevicePersistedStorage
) : DecomposeViewModel() {
    abstract fun getDevicesFlow(): StateFlow<ImmutableList<ConnectionSearchItem>>
    fun onDeviceClick(searchItem: ConnectionSearchItem) {
        viewModelScope.launch {
            if (searchItem.isAdded) {
                persistedStorage.removeDevice(searchItem.deviceModel.uniqueId)
            } else {
                persistedStorage.addDevice(searchItem.deviceModel)
            }
        }
    }
}