package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.service.FlipperInformationApi
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.di.BridgeImplComponent
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MAX_BATTERY_LEVEL = 100

class FlipperInformationApiImpl(
    private val scope: CoroutineScope
) : BluetoothGattServiceWrapper, FlipperInformationApi, LogTagProvider {
    override val TAG = "FlipperInformationApi"
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private var infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()

    @Inject
    lateinit var dataStoreFirstPair: Provider<DataStore<PairSettings>>

    init {
        ComponentHolder.component<BridgeImplComponent>().inject(this)
    }

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        getServiceOrLog(gatt, Constants.BLEInformationService.SERVICE_UUID)?.let { service ->
            infoCharacteristics.putAll(service.characteristics.map { it.uuid to it })
        }
        getServiceOrLog(gatt, Constants.GenericService.SERVICE_UUID)?.let { service ->
            infoCharacteristics.putAll(service.characteristics.map { it.uuid to it })
        }
        getServiceOrLog(gatt, Constants.BatteryService.SERVICE_UUID)?.let { service ->
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
        readInformationService(bleManager)
        readGenericService(bleManager)
        readBattery(bleManager)
    }

    private fun readInformationService(bleManager: UnsafeBleManager) {
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BLEInformationService.MANUFACTURER]
        ).with { _, data ->
            val content = data.value ?: return@with
            informationState.update {
                it.copy(manufacturerName = String(content))
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

    private fun readGenericService(bleManager: UnsafeBleManager) {
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.GenericService.DEVICE_NAME]
        ).with { _, data ->
            val content = data.value ?: return@with
            val deviceName = String(content)
            informationState.update {
                it.copy(deviceName)
            }
            onDeviceNameReceived(deviceName)
        }.enqueue()
    }

    private fun readBattery(bleManager: UnsafeBleManager) {
        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BatteryService.BATTERY_LEVEL]
        ).with { _, data ->
            val content = data.value ?: return@with
            val batteryLevel = content.firstOrNull() ?: return@with
            val batteryLevelAsFloat = batteryLevel.toFloat() / MAX_BATTERY_LEVEL
            if (batteryLevelAsFloat in 0.0f..1.0f) {
                informationState.update {
                    it.copy(batteryLevel = batteryLevelAsFloat)
                }
            }
        }.enqueue()
    }

    private fun onDeviceNameReceived(deviceName: String) {
        scope.launch {
            dataStoreFirstPair.get().updateData {
                var deviceNameFormatted = deviceName.trim()
                if (deviceNameFormatted.startsWith(Constants.DEVICENAME_PREFIX)) {
                    deviceNameFormatted = deviceNameFormatted
                        .replaceFirst(Constants.DEVICENAME_PREFIX, "")
                        .trim()
                }
                it.toBuilder()
                    .setDeviceName(deviceNameFormatted)
                    .build()
            }
        }
    }

    override fun reset(bleManager: UnsafeBleManager) {
        informationState.update { FlipperGATTInformation() }
    }

    override fun getInformationFlow(): StateFlow<FlipperGATTInformation> {
        return informationState
    }
}
