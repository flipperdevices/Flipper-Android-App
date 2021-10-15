package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import timber.log.Timber
import java.util.UUID

class FlipperBleManagerImpl(
    context: Context,
    private val scope: CoroutineScope
) : BleManager(context), FlipperBleManager {
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private val receiveBytesFlow = MutableSharedFlow<ByteArray>()
    private val infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()
    private var serialTxCharacteristic: BluetoothGattCharacteristic? = null
    private var serialRxCharacteristic: BluetoothGattCharacteristic? = null

    override val flipperRequestApi: FlipperRequestApi = FlipperRequestApiImpl(this, scope)
    override val isDeviceConnected = super.isConnected()
    override fun getInformationStateFlow(): StateFlow<FlipperGATTInformation> = informationState
    override fun getConnectionStateFlow(): StateFlow<ConnectionState> = stateAsFlow()
    override fun disconnectDevice() = disconnect().enqueue()
    override fun receiveBytesFlow(): Flow<ByteArray> {
        return receiveBytesFlow
    }

    override fun sendBytes(data: ByteArray) {
        writeCharacteristic(serialTxCharacteristic, data).enqueue()
    }

    override fun connectToDevice(device: BluetoothDevice) {
        connect(device).retry(
            Constants.BLE.RECONNECT_COUNT,
            Constants.BLE.RECONNECT_TIME_MS.toInt()
        ).useAutoConnect(true)
            .enqueue()
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
            registerToInformationGATT()
            registerToSerialGATT()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            gatt.services.forEach { service ->
                service.characteristics.forEach {
                    Timber.i("Characteristic for service ${service.uuid}: ${it.uuid}")
                }
            }

            val informationService =
                gatt.getService(Constants.BLEInformationService.SERVICE_UUID)

            informationService?.characteristics?.forEach {
                infoCharacteristics[it.uuid] = it
            }

            val serialService =
                gatt.getService(Constants.BLESerialService.SERVICE_UUID)

            serialTxCharacteristic = serialService?.getCharacteristic(Constants.BLESerialService.TX)
            serialRxCharacteristic = serialService?.getCharacteristic(Constants.BLESerialService.RX)

            val genericService = gatt.getService(Constants.GenericService.SERVICE_UUID)

            genericService?.characteristics?.find {
                it.uuid.equals(Constants.GenericService.DEVICE_NAME)
            }?.let {
                infoCharacteristics[Constants.GenericService.DEVICE_NAME] = it
            }

            return true
        }

        override fun onServicesInvalidated() {
            // TODO reset state
        }
    }

    @DelicateCoroutinesApi // TODO replace it
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

    private fun registerToInformationGATT() {
        readCharacteristic(
            infoCharacteristics[Constants.BLEInformationService.MANUFACTURER]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(manufacturerName = String(content))
            }
        }.enqueue()
        readCharacteristic(
            infoCharacteristics[Constants.GenericService.DEVICE_NAME]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(deviceName = String(content))
            }
        }.enqueue()
        readCharacteristic(
            infoCharacteristics[Constants.BLEInformationService.HARDWARE_VERSION]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(hardwareRevision = String(content))
            }
        }.enqueue()
        readCharacteristic(
            infoCharacteristics[Constants.BLEInformationService.SOFTWARE_VERSION]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(softwareVersion = String(content))
            }
        }.enqueue()
    }
}
