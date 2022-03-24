package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.flipperdevices.bridge.api.manager.service.FlipperInformationApi
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FlipperInformationApiImpl :
    BluetoothGattServiceWrapper, FlipperInformationApi, LogTagProvider {
    override val TAG = "FlipperInformationApi"
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private var infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        getServiceOrLog(gatt, Constants.BLEInformationService.SERVICE_UUID)?.let { service ->
            infoCharacteristics.putAll(service.characteristics.map { it.uuid to it })
        }
        getServiceOrLog(gatt, Constants.GenericService.SERVICE_UUID)?.let { service ->
            infoCharacteristics.putAll(service.characteristics.map { it.uuid to it })
        }
        return true
    }

    suspend fun checkVersionSupport(bleManager: UnsafeBleManager): Boolean {
        info { "Start version check" }

        val characteristic = infoCharacteristics[Constants.BLEInformationService.API_VERSION]
            ?: return false

        info { "Gatt service founded, start subscribe to gatt characteristic" }

        var apiVersion: String? = null
        bleManager.readCharacteristicUnsafe(characteristic)
            .with { _, data ->
                info { "Found information about version $data" }
                val content = data.value ?: return@with
                informationState.update {
                    it.copy(apiVersion = String(content))
                }
                apiVersion = String(content)
                info { "Api version is $apiVersion" }
            }
            .await()

        val apiVersionNumber = apiVersion?.toFloatOrNull() ?: return false
        info { "Parsed api version number is $apiVersionNumber" }
        return apiVersionNumber >= Constants.API_SUPPORTED_VERSION
    }

    override fun initialize(bleManager: UnsafeBleManager) {
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BLEInformationService.API_VERSION]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(apiVersion = String(content))
            }
        }
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
