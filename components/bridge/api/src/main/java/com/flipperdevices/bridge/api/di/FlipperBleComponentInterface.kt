package com.flipperdevices.bridge.api.di

import android.bluetooth.BluetoothAdapter
import com.flipperdevices.bridge.api.scanner.FlipperScanner

interface FlipperBleComponentInterface {
    val flipperScanner: FlipperScanner
    val bluetoothAdapter: BluetoothAdapter
}
