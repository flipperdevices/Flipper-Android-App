package com.flipper.bridge.impl.scanner

import com.flipper.bridge.api.scanner.FlipperScanner
import com.flipper.bridge.utils.Constants
import com.flipper.core.models.BLEDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings

class FlipperScannerImpl : FlipperScanner {
    private val scanner = BluetoothLeScannerCompat.getScanner()

    override fun findFlipperDevices(): Flow<Iterable<BLEDevice>> {
        val hashSet = hashSetOf<BLEDevice>()
        return scanner.scanFlow(provideSettings(), provideFilter())
            .filter { it.device.name?.startsWith(Constants.DEVICENAME_PREFIX) == true }
            .map { scanResult ->
                BLEDevice(
                    scanResult.device.address,
                    scanResult.device.name
                )
            }.map {
                hashSet.add(it)
                hashSet
            }
    }

    private fun provideSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setUseHardwareBatchingIfSupported(true)
            .build()
    }

    private fun provideFilter(): List<ScanFilter> {
        return emptyList()
        /*return listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(Constants.HEARTRATE_SERVICE_UUID))
                .build()
        )*/
    }
}
