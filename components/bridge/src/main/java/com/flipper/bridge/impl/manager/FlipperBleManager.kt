package com.flipper.bridge.impl.manager

import android.bluetooth.BluetoothGatt
import android.content.Context
import no.nordicsemi.android.ble.BleManager

class FlipperBleManager(context: Context) : BleManager(context) {
    override fun getGattCallback(): BleManagerGattCallback = FlipperBleManagerGattCallback()

    private class FlipperBleManagerGattCallback : BleManagerGattCallback() {
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            return true
        }

        override fun onServicesInvalidated() {

        }
    }
}