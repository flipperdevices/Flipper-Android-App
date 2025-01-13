package com.flipperdevices.bridge.connection.screens.search

import android.annotation.SuppressLint
import android.content.Context
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.FlipperZeroBle
import com.squareup.anvil.annotations.ContributesBinding
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
@ContributesBinding(AppGraph::class, ConnectionSearchViewModel::class)
class BLESearchViewModel @Inject constructor(
    context: Context,
    persistedStorage: FDevicePersistedStorage
) : ConnectionSearchViewModel(persistedStorage) {
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
            searchDevices.map { bleDevice ->
                ConnectionSearchItem(
                    address = bleDevice.address,
                    deviceModel = existedMacAddresses[bleDevice.address]
                        ?: bleDevice.toFDeviceFlipperZeroBleModel(),
                    isAdded = existedMacAddresses.containsKey(bleDevice.address)
                )
            }
        }.onEach { devicesFlow.emit(it.toPersistentList()) }
            .launchIn(viewModelScope)
    }

    override fun getDevicesFlow() = devicesFlow.asStateFlow()
}

private fun ServerDevice.toFDeviceFlipperZeroBleModel() = FDeviceFlipperZeroBleModel(
    name = this.name?.replaceFirst(Constants.DEVICENAME_PREFIX, "")
        ?.trim() ?: this.address,
    address = this.address,
    hardwareColor = FlipperZeroBle.HardwareColor.WHITE
)
