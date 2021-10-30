package com.flipperdevices.bridge.impl.scanner

import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings

class FlipperScannerImpl @Inject constructor(
    private val scanner: BluetoothLeScannerCompat
) : FlipperScanner {
    @ExperimentalCoroutinesApi
    override fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>> {
        val devices = arrayListOf<DiscoveredBluetoothDevice>()
        return scanner.scanFlow(provideSettings(), provideFilterForDefaultScan())
            .filter { it.device.name?.startsWith(Constants.DEVICENAME_PREFIX) == true }
            .map { scanResult ->
                val device = DiscoveredBluetoothDevice(scanResult)
                val alreadyExistDBD = devices.getOrNull(devices.indexOf(device))
                if (alreadyExistDBD != null) {
                    alreadyExistDBD.update(scanResult)
                } else {
                    devices.add(device)
                }
                devices
            }
    }

    @ExperimentalCoroutinesApi
    override fun findFlipperById(deviceId: String): Flow<DiscoveredBluetoothDevice> {
        return scanner.scanFlow(provideSettings(), provideFilterForFindById(deviceId))
            .map { DiscoveredBluetoothDevice(it) }
    }

    private fun provideSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setUseHardwareBatchingIfSupported(true)
            .build()
    }

    private fun provideFilterForDefaultScan(): List<ScanFilter> {
        return emptyList()
    }

    private fun provideFilterForFindById(deviceId: String): List<ScanFilter> {
        return listOf(ScanFilter.Builder().setDeviceAddress(deviceId).build())
    }
}
