package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.delegates.FlipperConnectionInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperRequestApiImpl
import com.flipperdevices.core.ktx.jre.newSingleThreadExecutor
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.ConnectionPriorityRequest
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow

@Suppress("BlockingMethodInNonBlockingContext")
class FlipperBleManagerImpl constructor(
    context: Context,
    scope: CoroutineScope
) : UnsafeBleManager(context), FlipperBleManager, LogTagProvider {
    override val TAG = "FlipperBleManager"
    private val bleDispatcher = newSingleThreadExecutor("FlipperBleManagerImpl")
        .asCoroutineDispatcher()

    // Gatt Delegates
    override val informationApi = FlipperInformationApiImpl()
    override val flipperRequestApi = FlipperRequestApiImpl(scope)

    // Manager delegates
    override val connectionInformationApi = FlipperConnectionInformationApiImpl(this)

    init {
        setConnectionObserver(ConnectionObserverLogger())
        info { "FlipperBleManagerImpl: ${this.hashCode()}" }
    }

    override suspend fun disconnectDevice() = withContext(bleDispatcher) {
        disconnect().enqueue()
        // Wait until device is really disconnected
        stateAsFlow().filter { it is ConnectionState.Disconnected }.first()
        return@withContext
    }

    override suspend fun connectToDevice(device: BluetoothDevice) = withContext(bleDispatcher) {
        connect(device).retry(
            Constants.BLE.RECONNECT_COUNT,
            Constants.BLE.RECONNECT_TIME_MS.toInt()
        ).useAutoConnect(true)
            .await()
    }

    override fun log(priority: Int, message: String) {
        info { "From BleManager: $message" }
    }

    override fun getGattCallback(): BleManagerGattCallback =
        FlipperBleManagerGattCallback()

    private inner class FlipperBleManagerGattCallback :
        BleManagerGattCallback() {

        override fun initialize() {
            if (!isBonded) {
                info { "Start bond insecure" }
                createBondInsecure().enqueue()
            }
        }

        override fun onDeviceReady() {
            // Set up large MTU
            // Also does not work with small MTU because of a bug in Flipper Zero firmware
            requestMtu(Constants.BLE.MTU).enqueue()
            requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue()

            informationApi.initialize(this@FlipperBleManagerImpl)
            flipperRequestApi.initialize(this@FlipperBleManagerImpl)
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            gatt.services.forEach { service ->
                service.characteristics.forEach {
                    debug { "Characteristic for service ${service.uuid}: ${it.uuid}" }
                }
            }

            flipperRequestApi.onServiceReceived(
                gatt.getService(Constants.BLESerialService.SERVICE_UUID)
            )
            informationApi.onServiceReceived(
                gatt.getService(Constants.BLEInformationService.SERVICE_UUID)
            )
            informationApi.onServiceReceived(
                gatt.getService(Constants.GenericService.SERVICE_UUID)
            )

            return true
        }

        override fun onServicesInvalidated() {
            informationApi.reset(this@FlipperBleManagerImpl)
            flipperRequestApi.reset(this@FlipperBleManagerImpl)
        }
    }
}
