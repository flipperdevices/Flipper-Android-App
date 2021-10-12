package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.status.pingRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.UUID

class FlipperBleManagerImpl(context: Context) : BleManager(context), FlipperBleManager {
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private val echoText = MutableStateFlow(ByteArray(0))
    private val infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()
    private var serialTxCharacteristic: BluetoothGattCharacteristic? = null
    private var serialRxCharacteristic: BluetoothGattCharacteristic? = null
    private val responseReader = PeripheralResponseReader()

    override val isDeviceConnected = super.isConnected()
    override fun getInformationStateFlow(): StateFlow<FlipperGATTInformation> = informationState
    override fun getEchoStateFlow(): StateFlow<ByteArray> = echoText
    override fun getConnectionStateFlow(): StateFlow<ConnectionState> = stateAsFlow()
    override fun disconnectDevice() = disconnect().enqueue()
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

    override fun sendEcho(text: String) {
        val protobufMessage = ByteArrayOutputStream().use { os ->
            main {
                commandId = 999
                pingRequest = pingRequest { }
            }.writeDelimitedTo(os)
            return@use os.toByteArray()
        }

        writeCharacteristic(serialTxCharacteristic, protobufMessage).enqueue()
    }

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
            responseReader.onReceiveBytes(bytes)
        }
        enableNotifications(serialRxCharacteristic).enqueue()
        enableIndications(serialRxCharacteristic).enqueue()

        GlobalScope.launch {
            responseReader.getResponses().collect {
                val response = it ?: return@collect
                Timber.i("Receive response: $response")
            }
        }
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
