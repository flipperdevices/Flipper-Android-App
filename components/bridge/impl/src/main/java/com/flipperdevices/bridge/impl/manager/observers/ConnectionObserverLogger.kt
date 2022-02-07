package com.flipperdevices.bridge.impl.manager.observers

import android.bluetooth.BluetoothDevice
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import no.nordicsemi.android.ble.observer.ConnectionObserver

class ConnectionObserverLogger(
    override val TAG: String
) : ConnectionObserver, LogTagProvider {
    override fun onDeviceConnecting(device: BluetoothDevice) {
        info { "#onDeviceConnecting $device" }
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        info { "#onDeviceConnected $device" }
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        info { "#onDeviceFailedToConnect $device, reason: $reason" }
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        info { "#onDeviceReady $device" }
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        info { "#onDeviceDisconnecting $device" }
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        info { "#onDeviceDisconnected $device, reason: $reason" }
    }
}
