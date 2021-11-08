package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.flipperdevices.bridge.api.manager.service.FlipperInformationApi
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FlipperInformationApiImpl :
    BluetoothGattServiceWrapper, FlipperInformationApi, LogTagProvider {
    override val TAG = "FlipperInformationApi"
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private var infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()

    override fun onServiceReceived(service: BluetoothGattService) {
        infoCharacteristics.putAll(service.characteristics.map { it.uuid to it })
    }

    override fun initialize(bleManager: UnsafeBleManager) {
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BLEInformationService.MANUFACTURER]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(manufacturerName = String(content))
            }
        }.enqueue()
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.GenericService.DEVICE_NAME]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(deviceName = String(content))
            }
        }.enqueue()
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BLEInformationService.HARDWARE_VERSION]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(hardwareRevision = String(content))
            }
        }.enqueue()
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BLEInformationService.SOFTWARE_VERSION]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(softwareVersion = String(content))
            }
        }.enqueue()
    }

    override fun reset(bleManager: UnsafeBleManager) {
        informationState.update { FlipperGATTInformation() }
    }

    override fun getInformationFlow(): StateFlow<FlipperGATTInformation> {
        return informationState
    }
}
