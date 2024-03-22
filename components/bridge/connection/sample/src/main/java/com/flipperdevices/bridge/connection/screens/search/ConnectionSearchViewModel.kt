package com.flipperdevices.bridge.connection.screens.search

import android.annotation.SuppressLint
import android.content.Context
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanMode
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import javax.inject.Inject

@SuppressLint("MissingPermission")
class ConnectionSearchViewModel @Inject constructor(
    context: Context
) : DecomposeViewModel() {
    private val aggregator = BleScanResultAggregator()
    private val devicesFlow = MutableStateFlow<PersistentList<ServerDevice>>(persistentListOf())

    init {
        BleScanner(context).scan(
            settings = BleScannerSettings(
                scanMode = BleScanMode.SCAN_MODE_LOW_LATENCY,
                includeStoredBondedDevices = true
            )
        )
            .map { aggregator.aggregateDevices(it) }
            .map { list -> list.filter { it.hasName } }
            .onEach { devicesFlow.emit(it.toPersistentList()) }
            .launchIn(viewModelScope)
    }

    fun getDevicesFlow() = devicesFlow.asStateFlow()
}
