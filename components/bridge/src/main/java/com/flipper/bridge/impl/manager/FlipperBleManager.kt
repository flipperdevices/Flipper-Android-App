package com.flipper.bridge.impl.manager

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipper.bridge.model.FlipperGATTInformation
import com.flipper.bridge.utils.Constants
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BleManager
import timber.log.Timber
import java.util.UUID

class FlipperBleManager(context: Context) : BleManager(context) {
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private val echoText = MutableStateFlow(ByteArray(0))
    private val infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()
    private var serialTxCharacteristic: BluetoothGattCharacteristic? = null
    private var serialRxCharacteristic: BluetoothGattCharacteristic? = null

    fun getInformationState(): StateFlow<FlipperGATTInformation> = informationState
    fun getEchoState(): StateFlow<ByteArray> = echoText
    override fun log(priority: Int, message: String) {
        Timber.d(message)
    }

    init {
        setConnectionObserver(ConnectionObserverLogger())
    }

    override fun getGattCallback(): BleManagerGattCallback =
        FlipperBleManagerGattCallback()

    fun sendEcho(text: String) {
        writeCharacteristic(serialTxCharacteristic, text.toByteArray()).enqueue()
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
            val informationService =
                gatt.getService(Constants.BLEInformationService.SERVICE_UUID)

            informationService?.characteristics?.forEach {
                infoCharacteristics[it.uuid] = it
            }

            val serialService =
                gatt.getService(Constants.BLESerialService.SERVICE_UUID)

            serialTxCharacteristic = serialService?.getCharacteristic(Constants.BLESerialService.TX)
            serialRxCharacteristic = serialService?.getCharacteristic(Constants.BLESerialService.RX)

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
            GlobalScope.launch {
                echoText.emit(bytes)
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
            infoCharacteristics[Constants.BLEInformationService.DEVICE_NAME]
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
