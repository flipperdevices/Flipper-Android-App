package com.flipper.bridge.impl.scanner

import com.flipper.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipper.bridge.api.scanner.FlipperScanner
import com.flipper.bridge.di.FlipperBleComponentProvider
import com.flipper.bridge.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import javax.inject.Inject

class FlipperScannerImpl : FlipperScanner {
    @Inject
    lateinit var scanner: BluetoothLeScannerCompat

    init {
        FlipperBleComponentProvider.component.inject(this)
    }

    override fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>> {
        val hashSet = hashSetOf<DiscoveredBluetoothDevice>()
        return scanner.scanFlow(provideSettings(), provideFilter())
            .filter { it.device.name?.startsWith(Constants.DEVICENAME_PREFIX) == true }
            .map { scanResult ->
                DiscoveredBluetoothDevice(scanResult)
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
