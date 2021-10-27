package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import no.nordicsemi.android.ble.BleManager

/**
 * BleManager from nordic library use protected method
 * So we can't call it outside BleManager
 * And the BleManager becomes very big
 * This wrapper allows you to call protected methods to delegates
 */
abstract class UnsafeBleManager(context: Context) : BleManager(context) {
    fun readCharacteristicUnsafe(characteristic: BluetoothGattCharacteristic?) =
        readCharacteristic(characteristic)
}
