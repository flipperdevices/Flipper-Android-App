package com.flipperdevices.core.ktx.android

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Build

// https://developer.android.com/reference/android/bluetooth/BluetoothAdapter#getDefaultAdapter()
fun BluetoothManager?.getBluetoothAdapter(): BluetoothAdapter {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            @Suppress("DEPRECATION")
            this?.adapter ?: BluetoothAdapter.getDefaultAdapter()
        else ->
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
    }
}
