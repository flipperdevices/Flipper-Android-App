package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.manager.service.RestartRPCApi
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Provider

private const val WAIT_DISCONNECT_TIMEOUT_MS = 5 * 1000L // 5 ms

@SingleIn(FlipperBleServiceGraph::class)
@ContributesBinding(FlipperBleServiceGraph::class, RestartRPCApi::class)
class RestartRPCApiImpl @Inject constructor(
    serviceApiProvider: Provider<FlipperServiceApi>
) : RestartRPCApi, BluetoothGattServiceWrapper, LogTagProvider {
    override val TAG = "RestartRPCApi"

    private var bleManagerInternal: UnsafeBleManager? = null

    private var rpcStateCharacteristic: BluetoothGattCharacteristic? = null

    private val serviceApi by serviceApiProvider

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        val service = getServiceOrLog(
            gatt = gatt,
            uuid = Constants.BLESerialService.SERVICE_UUID
        ) ?: return false

        rpcStateCharacteristic = getCharacteristicOrLog(
            service,
            Constants.BLESerialService.RPC_STATE
        )

        return rpcStateCharacteristic != null
    }

    override suspend fun restartRpc() {
        info { "Request restart rpc" }
        val bleManager = bleManagerInternal
        if (bleManager == null) {
            error { "Can't restart rpc, bleManager is null" }
            return
        }
        bleManager.writeCharacteristicUnsafe(rpcStateCharacteristic, byteArrayOf(0))
            .enqueue()
        withTimeoutOrNull(WAIT_DISCONNECT_TIMEOUT_MS) {
            bleManager.stateAsFlow().filter { it !is ConnectionState.Ready }.first()
        }
        serviceApi.reconnect()
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        bleManagerInternal = bleManager
    }

    override suspend fun reset(bleManager: UnsafeBleManager) = Unit
}
