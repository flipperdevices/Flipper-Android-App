package com.flipperdevices.bridge.api.scanner

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import no.nordicsemi.android.support.v18.scanner.ScanResult

@Parcelize
data class DiscoveredBluetoothDevice(
    val device: BluetoothDevice,
    private var lastScanResult: ScanResult,
    private var nameInternal: String?,
    private var rssiInternal: Int,
    private var previousRssi: Int,
    private var highestRssiInternal: Int = Byte.MIN_VALUE.toInt()
) : Parcelable {
    // Wrapper for data variables
    val address: String get() = device.address
    val scanResult: ScanResult get() = lastScanResult
    val name: String? get() = nameInternal
    val rssi: Int get() = rssiInternal
    val highestRssi: Int get() = highestRssiInternal

    constructor(scanResult: ScanResult) : this(
        device = scanResult.device,
        lastScanResult = scanResult,
        nameInternal = scanResult.scanRecord?.deviceName,
        rssiInternal = scanResult.rssi,
        previousRssi = scanResult.rssi,
        highestRssiInternal = scanResult.rssi
    )

    fun update(scanResult: ScanResult) {
        lastScanResult = scanResult
        nameInternal = scanResult.scanRecord?.deviceName ?: nameInternal
        previousRssi = rssiInternal
        rssiInternal = scanResult.rssi
        if (highestRssi < rssi) {
            highestRssiInternal = rssi
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is DiscoveredBluetoothDevice) {
            return device.address == other.address
        }
        return super.equals(other)
    }

    override fun hashCode() = device.hashCode()
}
