package com.flipper.bridge.impl.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.flipper.bridge.impl.manager.FlipperBleManager
import com.flipper.bridge.model.FlipperGATTInformation
import com.flipper.bridge.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class FlipperViewModel(application: Application) : AndroidViewModel(application) {
    private val bleManager = FlipperBleManager(application)
    private var currentDevice: BluetoothDevice? = null
    private val allEchoAnswers = mutableListOf<ByteArray>()

    fun getEchoAnswers(): Flow<List<ByteArray>> {
        return bleManager.getEchoState().map {
            if (it.isEmpty()) {
                return@map allEchoAnswers
            }
            allEchoAnswers.add(it)
            return@map allEchoAnswers
        }
    }

    fun sendEcho(text: String) {
        bleManager.sendEcho(text)
    }

    fun getDeviceInformation(): StateFlow<FlipperGATTInformation> {
        return bleManager.getInformationState()
    }

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
            .retry(Constants.BLE.RECONNECT_COUNT, Constants.BLE.RECONNECT_TIME)
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
