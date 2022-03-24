package com.flipperdevices.bridge.api.manager.observers

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.annotation.DisconnectionReason

interface SuspendConnectionObserver {
    suspend fun onDeviceConnecting(device: BluetoothDevice)

    suspend fun onDeviceConnected(device: BluetoothDevice)

    suspend fun onDeviceFailedToConnect(
        device: BluetoothDevice,
        @DisconnectionReason reason: Int
    )

    suspend fun onDeviceReady(device: BluetoothDevice)

    suspend fun onDeviceDisconnecting(device: BluetoothDevice)

    suspend fun onDeviceDisconnected(
        device: BluetoothDevice,
        @DisconnectionReason reason: Int
    )
}
