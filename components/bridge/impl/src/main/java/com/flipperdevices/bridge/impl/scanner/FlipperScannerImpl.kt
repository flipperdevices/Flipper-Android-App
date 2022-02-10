package com.flipperdevices.bridge.impl.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings

@ContributesBinding(AppGraph::class, FlipperScanner::class)
class FlipperScannerImpl @Inject constructor(
    private val scanner: BluetoothLeScannerCompat,
    private val context: Context
) : FlipperScanner, LogTagProvider {
    override val TAG = "FlipperScanner"

    override fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>> {
        val devices = arrayListOf<DiscoveredBluetoothDevice>()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException(
                "You should request BLUETOOTH_CONNECT before on Android API > 31"
            )
        }

        return scanner.scanFlow(provideSettings(), provideFilterForDefaultScan())
            .filter { it.device.name?.startsWith(Constants.DEVICENAME_PREFIX) == true }
            .map { scanResult ->
                val device = DiscoveredBluetoothDevice(scanResult)
                val alreadyExistDBD = devices.getOrNull(devices.indexOf(device))
                if (alreadyExistDBD != null) {
                    alreadyExistDBD.update(scanResult)
                } else {
                    info { "Find new device $scanResult" }
                    devices.add(device)
                }
                devices
            }
    }

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
