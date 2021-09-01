package com.flipper.bridge.impl.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.flipper.bridge.impl.manager.FlipperBleManager

class FlipperViewModel(application: Application) : AndroidViewModel(application) {
    private val bleManager = FlipperBleManager(application)
    private var currentDevice: BluetoothDevice? = null

    /**
     * Connect to the given peripheral.
     *
     * @param target the target device.
     */
    fun connect(target: BluetoothDevice) {
        // Prevent from calling again when called again (screen orientation changed).
        if (currentDevice == null) {
            currentDevice = target
            reconnect()
        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    fun reconnect() {
        val device = currentDevice ?: return
        bleManager.connect(device)
            .retry(3, 100)
            .useAutoConnect(false)
            .enqueue()
    }

    /**
     * Disconnect from peripheral.
     */
    private fun disconnect() {
        currentDevice = null
        bleManager.disconnect().enqueue()
    }

    override fun onCleared() {
        super.onCleared()
        if (bleManager.isConnected) {
            disconnect()
        }
    }
}