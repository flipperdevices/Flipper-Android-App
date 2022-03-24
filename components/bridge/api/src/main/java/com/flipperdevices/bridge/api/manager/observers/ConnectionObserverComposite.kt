package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.observer.ConnectionObserver

class ConnectionObserverComposite(
    private val scope: CoroutineScope,
    vararg initialObservers: SuspendConnectionObserver
) : ConnectionObserver {
    @Suppress("SpreadOperator")
    private val observers = mutableListOf(*initialObservers)
    private val compositeDispatcher = Dispatchers.Default.limitedParallelism(1)

    fun addObserver(observer: SuspendConnectionObserver) {
        if (observers.contains(observer)) {
            return
        }
        observers.add(observer)
    }

    fun removeObserver(observer: SuspendConnectionObserver) {
        observers.remove(observer)
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEach { it.onDeviceConnecting(device) }
        }
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEach { it.onDeviceConnected(device) }
        }
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        scope.launch(compositeDispatcher) {
            observers.forEach { it.onDeviceFailedToConnect(device, reason) }
        }
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEach { it.onDeviceReady(device) }
        }
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEach { it.onDeviceDisconnecting(device) }
        }
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        scope.launch(compositeDispatcher) {
            observers.forEach { it.onDeviceDisconnected(device, reason) }
        }
    }
}
