package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.observer.ConnectionObserver

class ConnectionObserverComposite(
    vararg initialObservers: ConnectionObserver
) : ConnectionObserver {
    @Suppress("SpreadOperator")
    private val observers = mutableListOf(*initialObservers)

    fun addObserver(observer: ConnectionObserver) {
        if (observers.contains(observer)) {
            return
        }
        observers.add(observer)
    }

    fun removeObserver(observer: ConnectionObserver) {
        observers.remove(observer)
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
        observers.forEach { it.onDeviceConnecting(device) }
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        observers.forEach { it.onDeviceConnected(device) }
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        observers.forEach { it.onDeviceFailedToConnect(device, reason) }
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        observers.forEach { it.onDeviceReady(device) }
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        observers.forEach { it.onDeviceDisconnecting(device) }
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        observers.forEach { it.onDeviceDisconnected(device, reason) }
    }
}
