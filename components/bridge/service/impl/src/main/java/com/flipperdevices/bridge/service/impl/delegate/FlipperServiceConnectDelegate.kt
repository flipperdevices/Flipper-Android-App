package com.flipperdevices.bridge.service.impl.delegate

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.bridge.service.impl.model.SavedFlipperConnectionInfo
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import javax.inject.Inject
import javax.inject.Provider

class FlipperServiceConnectDelegate @Inject constructor(
    bleManagerProvider: Provider<FlipperBleManager>,
    contextProvider: Provider<Context>,
    scannerProvider: Provider<FlipperScanner>,
    adapterProvider: Provider<BluetoothAdapter>
) : LogTagProvider {
    override val TAG = "FlipperServiceConnectDelegate"

    private val mutex = Mutex()

    private val bleManager by bleManagerProvider
    private val context by contextProvider
    private val scanner by scannerProvider
    private val adapter by adapterProvider

    suspend fun reconnect(
        connectionInfo: SavedFlipperConnectionInfo
    ) = withLock(mutex, "reconnect") {
        // If we already connected to device, just ignore it
        disconnectInternal()

        // If Bluetooth disable, return exception
        if (!adapter.isEnabled) {
            throw BluetoothDisabledException()
        }

        if (PermissionHelper.getUngrantedPermission(
                context,
                PermissionHelper.getRequiredPermissions()
            ).isNotEmpty()
        ) {
            throw SecurityException(
                """
                For connect to Flipper via bluetooth you need grant permission for you application. 
                Please, check PermissionHelper#checkPermissions
                """.trimIndent()
            )
        }

        // We try find device in manual mode and connect with it
        findAndConnectToDeviceInternal(connectionInfo)
    }

    suspend fun disconnect() = withLock(mutex, "disconnect") {
        disconnectInternal()
    }

    private suspend fun disconnectInternal() {
        try {
            withTimeout(Constants.BLE.DISCONNECT_TIMEOUT_MS) {
                bleManager.disconnectDevice()
            }
        } catch (timeout: TimeoutCancellationException) {
            error(timeout.cause) {
                "Can't disconnect device with timeout" +
                        " ${Constants.BLE.DISCONNECT_TIMEOUT_MS}"
            }
        }
    }

    // All rights must be obtained before calling this method
    @SuppressLint("MissingPermission")
    private suspend fun findAndConnectToDeviceInternal(
        connectionInfo: SavedFlipperConnectionInfo
    ) {
        var device = adapter.bondedDevices.find { it.address == connectionInfo.id }

        if (device == null) {
            device = withTimeout(Constants.BLE.CONNECT_TIME_MS) {
                scanner.findFlipperById(connectionInfo.id).first()
            }.device
        }

        val connectWithTimeout = runCatching {
            withTimeout(Constants.BLE.CONNECT_TIME_MS) {
                bleManager.connectToDevice(device)
            }
        }
        val exception = connectWithTimeout.exceptionOrNull()
        if (exception != null) {
            fallbackConnectByName(connectionInfo, exception)
        }
    }

    /**
     * It's a fallback if the user's MAC address has changed, but the flipper is the same.
     * This can happen in two cases:
     * - Changing official firmware to custom firmware (and back again)
     * - Updating official firmware after PR https://github.com/flipperdevices/flipperzero-firmware/pull/3723
     */
    private suspend fun fallbackConnectByName(
        connectionInfo: SavedFlipperConnectionInfo,
        exception: Throwable
    ) {
        if (connectionInfo.name == null) {
            error(exception) { "Failed connect by ID and flipper name is unknown" }
            throw exception
        }

        val device = withTimeout(Constants.BLE.CONNECT_TIME_MS * 10) {
            scanner.findFlipperByName(connectionInfo.name).filter {
                it.address != connectionInfo.id
            }.first()
        }.device
        info { "Found: $device" }

        //RemoveBondHelper.removeBand(device)
        bleManager.disconnectDevice()

        withTimeout(Constants.BLE.CONNECT_TIME_MS * 10) {
            bleManager.connectToDevice(device)
        }
    }
}
