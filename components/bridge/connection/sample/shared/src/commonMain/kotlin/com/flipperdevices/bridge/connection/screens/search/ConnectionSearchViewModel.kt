package com.flipperdevices.bridge.connection.screens.search

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

class ConnectionSearchViewModel @Inject constructor(
    private val persistedStorage: FDevicePersistedStorage,
    searchDelegatesFactories: Set<ConnectionSearchDelegate.Factory>
) : DecomposeViewModel() {
    private val searchDelegates = searchDelegatesFactories.map { factory -> factory(viewModelScope) }
    private val combinedFlow = combine(
        searchDelegates.map { delegate -> delegate.getDevicesFlow() }
    ) { deviceLists ->
        deviceLists.flatMap { deviceList -> deviceList }.toImmutableList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, persistentListOf())

    fun getDevicesFlow() = combinedFlow

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
