package com.flipperdevices.bridge.impl.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperScanner::class)
class FlipperScannerImpl @Inject constructor(
    private val scanner: BluetoothLeScannerCompat,
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : FlipperScanner, LogTagProvider {
    override val TAG = "FlipperScanner"

    override fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>> {
        val devices = ArrayList(getAlreadyBondedDevices())
        val mutex = Mutex()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException(
                "You should request BLUETOOTH_CONNECT before on Android API > 31"
            )
        }

        return merge(
            devices.asFlow(),
            scanner.scanFlow(provideSettings(), provideFilterForDefaultScan()).map {
                DiscoveredBluetoothDevice(it)
            }
        ).filter {
            it.address.startsWith(Constants.MAC_PREFIX) ||
                it.name?.startsWith(Constants.DEVICENAME_PREFIX) == true
        }.map { discoveredBluetoothDevice ->
            var mutableDevicesList: List<DiscoveredBluetoothDevice> = emptyList()
            mutex.withLock {
                val alreadyExistDBD = devices.getOrNull(
                    devices.indexOf(discoveredBluetoothDevice)
                )
                if (alreadyExistDBD != null) {
                    val scanResult = discoveredBluetoothDevice.scanResult
                    if (scanResult != null) {
                        alreadyExistDBD.update(scanResult)
                    }
                } else {
                    info { "Find new device $discoveredBluetoothDevice" }
                    devices.add(discoveredBluetoothDevice)
                }
                mutableDevicesList = devices.toList()
            }
            return@map mutableDevicesList
        }
    }

    override fun findFlipperById(deviceId: String): Flow<DiscoveredBluetoothDevice> {
        val bondedDevice = getAlreadyBondedDevices().firstOrNull {
            it.address == deviceId
        }
        if (bondedDevice != null) {
            return flowOf(bondedDevice)
        }
        return scanner.scanFlow(provideSettings(), provideFilterForFindById(deviceId))
            .map { DiscoveredBluetoothDevice(it) }
    }

    private fun getAlreadyBondedDevices(): List<DiscoveredBluetoothDevice> {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return emptyList()
        }

        return bluetoothAdapter.bondedDevices.filter {
            it.address?.startsWith(Constants.MAC_PREFIX) == true ||
                it.name?.startsWith(Constants.DEVICENAME_PREFIX) == true
        }.map {
            DiscoveredBluetoothDevice(
                device = it,
                nameInternal = it.name,
                rssiInternal = 0,
                previousRssi = 0
            )
        }
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
