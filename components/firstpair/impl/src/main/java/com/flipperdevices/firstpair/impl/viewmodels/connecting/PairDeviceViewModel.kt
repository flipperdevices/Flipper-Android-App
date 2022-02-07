package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel

class PairDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val firstPairBleManager by lazy { FirstPairBleManager(getApplication()) }

    fun startConnectToDevice(device: BluetoothDevice) {
        firstPairBleManager.connect(device)
    }

    fun getConnectionState() {
    }
}
