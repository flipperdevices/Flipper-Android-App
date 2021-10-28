package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.delegates.FlipperConnectionInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperSerialApiImpl
import com.flipperdevices.core.utils.newSingleThreadExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import timber.log.Timber

@Suppress("BlockingMethodInNonBlockingContext")
class FlipperBleManagerImpl constructor(
    context: Context,
    scope: CoroutineScope
) : UnsafeBleManager(context), FlipperBleManager {
    private val bleDispatcher = newSingleThreadExecutor("FlipperBleManagerImpl")
        .asCoroutineDispatcher()

    // Gatt Delegates
    override val informationApi = FlipperInformationApiImpl()
    override val serialApi = FlipperSerialApiImpl(scope)
    override val flipperRequestApi = FlipperRequestApiImpl(serialApi, scope)

    // Manager delegates
    override val connectionInformationApi = FlipperConnectionInformationApiImpl(this)

    init {
        setConnectionObserver(ConnectionObserverLogger())
        Timber.i("FlipperBleManagerImpl: ${this.hashCode()}")
    }

    override suspend fun disconnectDevice() = withContext(bleDispatcher) {
        disconnect().enqueue()
        // Wait until device is really disconnected
        stateAsFlow().filter { it == ConnectionState.Disconnecting }.single()
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
        Timber.d(message)
    }

    override fun getGattCallback(): BleManagerGattCallback =
        FlipperBleManagerGattCallback()

    private inner class FlipperBleManagerGattCallback :
        BleManagerGattCallback() {

        override fun initialize() {
            if (!isBonded) {
                Timber.i("Start bond insecure")
                createBondInsecure().enqueue()
            }
        }

        override fun onDeviceReady() {
            // Set up large MTU
            // Also does not work with small MTU because of a bug in Flipper Zero firmware
            requestMtu(Constants.BLE.MTU).enqueue()

            informationApi.initialize(this@FlipperBleManagerImpl)
            serialApi.initialize(this@FlipperBleManagerImpl)
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            gatt.services.forEach { service ->
                service.characteristics.forEach {
                    Timber.d("Characteristic for service ${service.uuid}: ${it.uuid}")
                }
            }

            serialApi.onServiceReceived(
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
            informationApi.reset()
            serialApi.reset()
        }
    }
}
