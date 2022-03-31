package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import com.flipperdevices.core.ktx.jre.forEachIterable
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
        scope.launch {
            if (observers.contains(observer)) {
                return@launch
            }
            observers.add(observer)
        }
    }

    fun removeObserver(observer: SuspendConnectionObserver) {
        scope.launch {
            observers.remove(observer)
        }
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEachIterable { it.onDeviceConnecting(device) }
        }
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEachIterable { it.onDeviceConnected(device) }
        }
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        scope.launch(compositeDispatcher) {
            observers.forEachIterable { it.onDeviceFailedToConnect(device, reason) }
        }
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEachIterable { it.onDeviceReady(device) }
        }
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        scope.launch(compositeDispatcher) {
            observers.forEachIterable { it.onDeviceDisconnecting(device) }
        }
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        scope.launch(compositeDispatcher) {
            observers.forEachIterable { it.onDeviceDisconnected(device, reason) }
        }
    }
}
