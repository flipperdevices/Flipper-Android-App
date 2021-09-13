package com.flipper.bridge.api.scanner

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import no.nordicsemi.android.support.v18.scanner.ScanResult

@Parcelize
data class DiscoveredBluetoothDevice(
    val device: BluetoothDevice,
    private var lastScanResult: ScanResult,
    private var _name: String?,
    private var _rssi: Int,
    private var previousRssi: Int,
    private var _highestRssi: Int = Byte.MIN_VALUE.toInt()
) : Parcelable {
    // Wrapper for data variables
    val address: String get() = device.address
    val scanResult: ScanResult get() = lastScanResult
    val name: String? get() = _name
    val rssi: Int get() = _rssi
    val highestRssi: Int get() = _highestRssi


    constructor(scanResult: ScanResult) : this(
        device = scanResult.device,
        lastScanResult = scanResult,
        _name = scanResult.scanRecord?.deviceName,
        _rssi = scanResult.rssi,
        previousRssi = scanResult.rssi,
        _highestRssi = scanResult.rssi
    )

    fun update(scanResult: ScanResult) {
        lastScanResult = scanResult
        _name = scanResult.scanRecord?.deviceName ?: _name
        previousRssi = _rssi
        _rssi = scanResult.rssi
        if (highestRssi < rssi) {
            _highestRssi = rssi
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