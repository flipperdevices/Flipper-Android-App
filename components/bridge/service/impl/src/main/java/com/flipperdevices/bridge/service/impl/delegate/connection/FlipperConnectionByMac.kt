package com.flipperdevices.bridge.service.impl.delegate.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.impl.model.SavedFlipperConnectionInfo
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Provider

class FlipperConnectionByMac @Inject constructor(
    bleManagerProvider: Provider<FlipperBleManager>,
    scannerProvider: Provider<FlipperScanner>,
    adapterProvider: Provider<BluetoothAdapter>
) : FlipperConnectionDelegate, LogTagProvider {
    override val TAG = "FlipperConnectionByMac"

    private val bleManager by bleManagerProvider
    private val scanner by scannerProvider
    private val adapter by adapterProvider


    @SuppressLint("MissingPermission")
    override suspend fun connect(connectionInfo: SavedFlipperConnectionInfo): Boolean {
        info { "Start connection by $connectionInfo" }
        var device = adapter.bondedDevices.find { it.address == connectionInfo.id }

        if (device == null) {
            device = runCatching {
                withTimeout(Constants.BLE.CONNECT_TIME_MS) {
                    scanner.findFlipperById(connectionInfo.id).first()
                }.device
            }.getOrNull()
        }
        if (device == null) {
            return false
        }

        val connectWithTimeout = runCatching {
            withTimeout(Constants.BLE.CONNECT_TIME_MS) {
                bleManager.connectToDevice(device)
            }
        }
        val exception = connectWithTimeout.exceptionOrNull()

        if (exception != null) {
            error(exception) { "Failed connect to device by MAC" }
            return false
        }
        return true
    }

}