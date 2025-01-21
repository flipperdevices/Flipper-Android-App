package com.flipperdevices.bridge.api.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.UUID

data class DiscoveredBluetoothDevice(
    val device: BluetoothDevice,
    private var lastScanResult: ScanResult? = null,
    private var nameInternal: String?,
    private var rssiInternal: Int,
    private var previousRssi: Int,
    private var highestRssiInternal: Int = Byte.MIN_VALUE.toInt(),
    private var servicesResult: List<ParcelUuid>? = null
) {
    // Wrapper for data variables
    val address: String get() = device.address
    val scanResult: ScanResult? get() = lastScanResult
    val name: String? get() = nameInternal
    val rssi: Int get() = rssiInternal
    val highestRssi: Int get() = highestRssiInternal
    val services: List<UUID> get() = servicesResult?.map { it.uuid }.orEmpty()

    constructor(scanResult: ScanResult) : this(
        device = scanResult.device,
        lastScanResult = scanResult,
        nameInternal = scanResult.scanRecord?.deviceName
            ?: scanResult.device.getNameSafe(),
        rssiInternal = scanResult.rssi,
        previousRssi = scanResult.rssi,
        highestRssiInternal = scanResult.rssi,
        servicesResult = scanResult.scanRecord?.serviceUuids
    )

    fun update(scanResult: ScanResult) {
        lastScanResult = scanResult
        nameInternal = scanResult.scanRecord?.deviceName ?: nameInternal
        servicesResult = scanResult.scanRecord?.serviceUuids
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
        return false
    }

    override fun hashCode() = device.hashCode()
}

@SuppressLint("MissingPermission")
private fun BluetoothDevice.getNameSafe(): String? {
    return try {
        name
    } catch (ignored: Exception) {
        null
    }
}
