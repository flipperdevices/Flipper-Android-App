package com.flipperdevices.bridge.connection.transport.ble.impl.utils

import android.annotation.SuppressLint
import android.content.Context
import com.flipperdevices.bridge.connection.transport.ble.impl.utils.BleConstants.FAST_CONNECT_TIME_MS
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanMode
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import javax.inject.Inject

/**
 * This class aims to speed up the connection to a BLE device.
 * The main idea is that instead of searching for a device all over again,
 * we just pull it out of the already paired ones
 */
@SuppressLint("MissingPermission")
class BLEConnectionDeviceHelper @Inject constructor(
    private val scanner: BleScanner
) : LogTagProvider {
    override val TAG = "BLEConnectionDeviceHelper"

    suspend fun acceleratedBleConnect(
        context: Context,
        macAddress: String,
        scope: CoroutineScope
    ): ClientBleGatt {
        val pairedDevice = withTimeoutOrNull(FAST_CONNECT_TIME_MS) {
            scanner.scan(
                settings = BleScannerSettings(
                    scanMode = BleScanMode.SCAN_MODE_LOW_LATENCY,
                    includeStoredBondedDevices = true
                )
            ).filter { it.device.address == macAddress }
                .first()
        }
        if (pairedDevice == null) {
            info { "Fail to find paired device object, so fallback to connection by mac" }
            return fallbackConnection(
                context = context,
                macAddress = macAddress,
                scope = scope
            )
        } else {
            info { "Find paired device object $pairedDevice" }
            return fastConnection(
                context = context,
                device = pairedDevice.device,
                scope = scope
            )
        }
    }

    private suspend fun fastConnection(
        context: Context,
        device: ServerDevice,
        scope: CoroutineScope
    ): ClientBleGatt {
        return ClientBleGatt.connect(
            context = context,
            device = device,
            scope = scope,
            options = BleGattConnectOptions(
                autoConnect = true,
                closeOnDisconnect = false
            )
        )
    }

    private suspend fun fallbackConnection(
        context: Context,
        macAddress: String,
        scope: CoroutineScope
    ): ClientBleGatt {
        return ClientBleGatt.connect(
            context = context,
            macAddress = macAddress,
            scope = scope,
            options = BleGattConnectOptions(
                autoConnect = true,
                closeOnDisconnect = false
            )
        )
    }
}
