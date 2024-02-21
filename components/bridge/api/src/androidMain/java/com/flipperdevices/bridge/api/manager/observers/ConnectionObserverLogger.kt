package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

class ConnectionObserverLogger(
    override val TAG: String
) : SuspendConnectionObserver, LogTagProvider {
    override suspend fun onDeviceConnecting(device: BluetoothDevice) {
        info { "#onDeviceConnecting $device" }
    }

    override suspend fun onDeviceConnected(device: BluetoothDevice) {
        info { "#onDeviceConnected $device" }
    }

    override suspend fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        info { "#onDeviceFailedToConnect $device, reason: $reason" }
    }

    override suspend fun onDeviceReady(device: BluetoothDevice) {
        info { "#onDeviceReady $device" }
    }

    override suspend fun onDeviceDisconnecting(device: BluetoothDevice) {
        info { "#onDeviceDisconnecting $device" }
    }

    override suspend fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        info { "#onDeviceDisconnected $device, reason: $reason" }
    }
}
