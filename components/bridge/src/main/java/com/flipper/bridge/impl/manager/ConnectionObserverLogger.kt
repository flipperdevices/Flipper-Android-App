package com.flipper.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.observer.ConnectionObserver
import timber.log.Timber

class ConnectionObserverLogger : ConnectionObserver {
    override fun onDeviceConnecting(device: BluetoothDevice) {
        Timber.i("#onDeviceConnecting")
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        Timber.i("#onDeviceConnected")
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        Timber.i("#onDeviceFailedToConnect")
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        Timber.i("#onDeviceReady")
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        Timber.i("#onDeviceDisconnecting")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        Timber.i("#onDeviceDisconnected")
    }
}
