package com.flipper.bridge.utils.ble

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import timber.log.Timber

/**
 * Just heats up the BluetoothGatt stack. Useful to do before connecting to any device to decrease
 * the chances of connection failures (even if you just access the device directly via
 * its MAC address).
 */
class BleScanHeater {
    private val scanner = BluetoothLeScannerCompat.getScanner()

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            Timber.e("Scan failed. ErrorCode: $errorCode")
        }
    }

    fun register() {
        val scanSettings = ScanSettings.Builder().apply {
            setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        }.build()
        scanner.startScan(null, scanSettings, scanCallback)
    }

    fun unregister() {
        scanner.stopScan(scanCallback)
    }
}
