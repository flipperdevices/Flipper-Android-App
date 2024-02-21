package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import com.flipperdevices.core.ktx.jre.forEachIterable
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.observer.ConnectionObserver

class ConnectionObserverComposite(
    private val scope: CoroutineScope,
    vararg initialObservers: SuspendConnectionObserver
) : ConnectionObserver, LogTagProvider {
    override val TAG = "ConnectionObserverComposite"

    @Suppress("SpreadOperator")
    private val observers = mutableListOf(*initialObservers)

    @Synchronized
    fun addObserver(observer: SuspendConnectionObserver) {
        info { "Add observer $observer" }
        if (observers.contains(observer)) {
            return
        }
        observers.add(observer)
    }

    @Synchronized
    fun removeObserver(observer: SuspendConnectionObserver) {
        info { "Remove observer $observer" }

        observers.remove(observer)
    }

    @Synchronized
    override fun onDeviceConnecting(device: BluetoothDevice) {
        observers.forEachIterable {
            scope.launch(Dispatchers.Default) {
                it.onDeviceConnecting(device)
            }
        }
    }

    @Synchronized
    override fun onDeviceConnected(device: BluetoothDevice) {
        observers.forEachIterable {
            scope.launch(Dispatchers.Default) {
                it.onDeviceConnected(device)
            }
        }
    }

    @Synchronized
    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        observers.forEachIterable {
            scope.launch(Dispatchers.Default) {
                it.onDeviceFailedToConnect(device, reason)
            }
        }
    }

    @Synchronized
    override fun onDeviceReady(device: BluetoothDevice) {
        observers.forEachIterable {
            scope.launch(Dispatchers.Default) {
                it.onDeviceReady(device)
            }
        }
    }

    @Synchronized
    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        observers.forEachIterable {
            scope.launch(Dispatchers.Default) {
                it.onDeviceDisconnecting(device)
            }
        }
    }

    @Synchronized
    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        observers.forEachIterable {
            scope.launch(Dispatchers.Default) {
                it.onDeviceDisconnected(device, reason)
            }
        }
    }
}
