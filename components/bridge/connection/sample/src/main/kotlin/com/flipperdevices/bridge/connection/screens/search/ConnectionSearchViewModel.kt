package com.flipperdevices.bridge.connection.screens.search

import android.annotation.SuppressLint
import android.content.Context
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanMode
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import javax.inject.Inject

@SuppressLint("MissingPermission")
class ConnectionSearchViewModel @Inject constructor(
    context: Context,
    private val persistedStorage: FDevicePersistedStorage
) : DecomposeViewModel() {
    private val aggregator = BleScanResultAggregator()
    private val devicesFlow = MutableStateFlow<PersistentList<ConnectionSearchItem>>(
        persistentListOf()
    )

    init {
        combine(
            BleScanner(context).scan(
                settings = BleScannerSettings(
                    scanMode = BleScanMode.SCAN_MODE_LOW_LATENCY,
                    includeStoredBondedDevices = true
                )
            ).map { aggregator.aggregateDevices(it) }
                .map { list -> list.filter { it.hasName } },
            persistedStorage.getAllDevices()
        ) { searchDevices, savedDevices ->
            val existedMacAddresses = savedDevices
                .filterIsInstance<FDeviceFlipperZeroBleModel>()
                .associateBy { it.address }
            searchDevices.map {
                ConnectionSearchItem(
                    device = it,
                    savedDeviceModel = existedMacAddresses[it.address]
                )
            }
        }.onEach { devicesFlow.emit(it.toPersistentList()) }
            .launchIn(viewModelScope)
    }

    fun getDevicesFlow() = devicesFlow.asStateFlow()

    fun onDeviceClicked(searchItem: ConnectionSearchItem) {
        viewModelScope.launch {
            if (searchItem.savedDeviceModel == null) {
                persistedStorage.addDevice(searchItem.device.toFDeviceFlipperZeroBleModel())
            } else {
                persistedStorage.removeDevice(searchItem.savedDeviceModel.uniqueId)
            }
        }
    }
}

private fun ServerDevice.toFDeviceFlipperZeroBleModel() = FDeviceFlipperZeroBleModel(
    name = this.name?.replaceFirst(Constants.DEVICENAME_PREFIX, "")
        ?.trim() ?: this.address,
    address = this.address
)
