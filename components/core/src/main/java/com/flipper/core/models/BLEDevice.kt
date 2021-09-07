package com.flipper.core.models

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BLEDevice(
    val id: String,
    val name: String
) : Parcelable {
    fun getBluetoothDevice(adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()): BluetoothDevice {
        check(!BluetoothAdapter.checkBluetoothAddress(id)) { "Invalid bluetooth address" }
        return adapter.getRemoteDevice(id)
    }
}
