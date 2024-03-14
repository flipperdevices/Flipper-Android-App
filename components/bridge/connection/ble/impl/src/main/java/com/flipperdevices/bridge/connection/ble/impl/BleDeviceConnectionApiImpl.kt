package com.flipperdevices.bridge.connection.ble.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import com.flipperdevices.bridge.connection.ble.api.BleDeviceConnectionApi
import com.flipperdevices.bridge.connection.ble.api.FBleApi
import com.flipperdevices.bridge.connection.ble.api.FBleDeviceConnectionConfig
import com.flipperdevices.bridge.connection.ble.impl.model.BLEConnectionPermissionException
import com.flipperdevices.bridge.connection.ble.impl.model.FailedConnectToDeviceException
import com.flipperdevices.bridge.connection.ble.impl.utils.BleConstants
import com.flipperdevices.bridge.connection.ble.impl.utils.TimberBleLogger
import com.flipperdevices.bridge.connection.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.BleDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanFilter
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanMode
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner

@ContributesBinding(AppGraph::class, BleDeviceConnectionApi::class)
class BleDeviceConnectionApiImpl @Inject constructor(
    private val context: Context
) : BleDeviceConnectionApi {

    override suspend fun connect(
        scope: CoroutineScope,
        config: FBleDeviceConnectionConfig
    ): Result<FBleApi> = runCatching {
        return@runCatching connectUnsafe(scope, config)
    }

    private suspend fun connectUnsafe(
        scope: CoroutineScope,
        config: FBleDeviceConnectionConfig
    ): FBleApi {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            throw BLEConnectionPermissionException()
        }
        val device = withTimeout(BleConstants.CONNECT_TIME_MS) {
            ClientBleGatt.connect(
                context = context,
                macAddress = config.macAddress,
                scope = scope,
                options = BleGattConnectOptions(
                    autoConnect = true
                ),
                logger = TimberBleLogger()
            )
        }
        if (!device.isConnected) {
            throw FailedConnectToDeviceException()
        }
        device.requestMtu(BleConstants.MAX_MTU)
        device.discoverServices()

        return FBleApiImpl(scope, device)
    }
}