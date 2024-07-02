package com.flipperdevices.bridge.service.impl.delegate

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.bridge.service.impl.delegate.connection.FlipperConnectionByMac
import com.flipperdevices.bridge.service.impl.delegate.connection.FlipperConnectionByName
import com.flipperdevices.bridge.service.impl.delegate.connection.FlipperConnectionDelegate
import com.flipperdevices.bridge.service.impl.model.SavedFlipperConnectionInfo
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import javax.inject.Inject
import javax.inject.Provider

class FlipperServiceConnectDelegate @Inject constructor(
    bleManagerProvider: Provider<FlipperBleManager>,
    contextProvider: Provider<Context>,
    adapterProvider: Provider<BluetoothAdapter>,
    flipperConnectionByMac: Provider<FlipperConnectionByMac>,
    flipperConnectionByName: Provider<FlipperConnectionByName>
) : LogTagProvider {
    override val TAG = "FlipperServiceConnectDelegate"

    private val mutex = Mutex()

    private val bleManager by bleManagerProvider
    private val context by contextProvider
    private val adapter by adapterProvider

    private val connectionDelegates = listOf<FlipperConnectionDelegate>(
        flipperConnectionByMac.get(),
        flipperConnectionByName.get()
    )

    suspend fun reconnect(
        connectionInfo: SavedFlipperConnectionInfo
    ): Boolean = withLockResult(mutex, "reconnect") {
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
        return@withLockResult findAndConnectToDeviceInternal(connectionInfo)
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
    ): Boolean {
        for (delegate in connectionDelegates) {
            if (delegate.connect(connectionInfo)) {
                return true
            }
        }
        return false
    }
}
