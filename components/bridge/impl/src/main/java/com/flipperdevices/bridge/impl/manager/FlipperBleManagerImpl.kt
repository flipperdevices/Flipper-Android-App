package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.delegates.FlipperConnectionInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.core.utils.newSingleThreadExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Suppress("BlockingMethodInNonBlockingContext")
class FlipperBleManagerImpl(
    context: Context,
    private val scope: CoroutineScope
) : UnsafeBleManager(context), FlipperBleManager {
    private val bleDispatcher = newSingleThreadExecutor("FlipperBleManagerImpl")
        .asCoroutineDispatcher()

    // Gatt Delegates
    override val informationApi = FlipperInformationApiImpl()
    override val flipperRequestApi = FlipperRequestApiImpl(this, scope)

    // Manager delegates
    override val connectionInformationApi = FlipperConnectionInformationApiImpl(this)

    private val receiveBytesFlow = MutableSharedFlow<ByteArray>()
    private var serialTxCharacteristic: BluetoothGattCharacteristic? = null
    private var serialRxCharacteristic: BluetoothGattCharacteristic? = null

    override suspend fun disconnectDevice() = withContext(bleDispatcher) {
        disconnect().await()
    }

    override fun receiveBytesFlow(): Flow<ByteArray> {
        return receiveBytesFlow
    }

    override fun sendBytes(data: ByteArray) {
        writeCharacteristic(serialTxCharacteristic, data).enqueue()
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

    init {
        setConnectionObserver(ConnectionObserverLogger())
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
            registerToSerialGATT()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            gatt.services.forEach { service ->
                service.characteristics.forEach {
                    Timber.d("Characteristic for service ${service.uuid}: ${it.uuid}")
                }
            }

            val serialService =
                gatt.getService(Constants.BLESerialService.SERVICE_UUID)

            serialTxCharacteristic = serialService?.getCharacteristic(Constants.BLESerialService.TX)
            serialRxCharacteristic = serialService?.getCharacteristic(Constants.BLESerialService.RX)

            informationApi.onServiceReceived(gatt.getService(Constants.GenericService.SERVICE_UUID))

            return true
        }

        override fun onServicesInvalidated() {
            informationApi.initialize(this@FlipperBleManagerImpl)
        }
    }

    private fun registerToSerialGATT() {
        setNotificationCallback(serialRxCharacteristic).with { _, data ->
            Timber.i("Receive serial data")
            val bytes = data.value ?: return@with
            scope.launch {
                receiveBytesFlow.emit(bytes)
            }
        }
        enableNotifications(serialRxCharacteristic).enqueue()
        enableIndications(serialRxCharacteristic).enqueue()
    }
}
