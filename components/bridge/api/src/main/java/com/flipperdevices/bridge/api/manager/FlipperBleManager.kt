package com.flipperdevices.bridge.api.manager

import android.bluetooth.BluetoothDevice
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.ktx.state.ConnectionState

interface FlipperBleManager {
    val isDeviceConnected: Boolean
    fun getInformationStateFlow(): StateFlow<FlipperGATTInformation>
    fun getEchoStateFlow(): StateFlow<ByteArray>
    fun sendEcho(text: String)
    fun getConnectionStateFlow(): StateFlow<ConnectionState>
    fun connectToDevice(device: BluetoothDevice)
    fun disconnectDevice()
}
