package com.flipper.bridge.impl.manager

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipper.bridge.model.FlipperGATTInformation
import com.flipper.bridge.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import no.nordicsemi.android.ble.BleManager
import java.util.*

class FlipperBleManager(context: Context) : BleManager(context) {
    private val informationState = MutableStateFlow(FlipperGATTInformation())

    fun getInformationState() = informationState

    override fun getGattCallback(): BleManagerGattCallback =
        FlipperBleManagerGattCallback()

    private inner class FlipperBleManagerGattCallback :
        BleManagerGattCallback() {
        private val collectedCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()

        override fun initialize() {
            readCharacteristic(collectedCharacteristics[Constants.BLEInformationService.MANUFACTURER]).with { _, data ->
                val content = data.value ?: return@with
                informationState.update {
                    it.copy(manufacturerName = String(content))
                }
            }.enqueue()
            readCharacteristic(collectedCharacteristics[Constants.BLEInformationService.DEVICE_NAME]).with { _, data ->
                val content = data.value ?: return@with
                informationState.update {
                    it.copy(deviceName = String(content))
                }
            }.enqueue()
            readCharacteristic(collectedCharacteristics[Constants.BLEInformationService.HARDWARE_VERSION]).with { _, data ->
                val content = data.value ?: return@with
                informationState.update {
                    it.copy(hardwareRevision = String(content))
                }
            }.enqueue()
            readCharacteristic(collectedCharacteristics[Constants.BLEInformationService.SOFTWARE_VERSION]).with { _, data ->
                val content = data.value ?: return@with
                informationState.update {
                    it.copy(softwareVersion = String(content))
                }
            }.enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val informationService =
                gatt.getService(Constants.BLEInformationService.SERVICE_UUID) ?: return false

            informationService.characteristics.forEach {
                collectedCharacteristics[it.uuid] = it
            }

            return true
        }

        override fun onServicesInvalidated() {
            // TODO reset state
        }
    }
}
