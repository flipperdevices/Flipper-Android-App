package com.flipper.info.main.service

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.impl.manager.FlipperBleManager
import com.flipper.bridge.model.FlipperGATTInformation
import com.flipper.bridge.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FlipperViewModel(application: Application) : AndroidViewModel(application) {
    private val bleManager = FlipperBleManager(application)
    private var currentDevice: BluetoothDevice? = null
    private val echoAnswers = MutableStateFlow(emptyList<ByteArray>())
    private val allEchoAnswers = mutableListOf<ByteArray>()

    init {
        viewModelScope.launch {
            bleManager.getEchoState().collect {
                if (it.isEmpty()) {
                    return@collect
                }
                allEchoAnswers.add(it)
                echoAnswers.emit(ArrayList(allEchoAnswers))
            }
        }
    }

    fun getEchoAnswers(): StateFlow<List<ByteArray>> {
        return echoAnswers
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
