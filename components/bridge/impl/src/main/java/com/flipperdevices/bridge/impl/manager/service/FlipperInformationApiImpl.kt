package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.service.FlipperInformationApi
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.utils.Shake2ReportHelper
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.FlipperGattInfoEvent
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import no.nordicsemi.android.ble.data.Data
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import kotlin.experimental.and

private const val MAX_BATTERY_LEVEL = 100

class FlipperInformationApiImpl @Inject constructor(
    scopeProvider: Provider<CoroutineScope>,
    metricApiProvider: Provider<MetricApi>,
    dataStoreFirstPairProvider: Provider<DataStore<PairSettings>>,
    shake2ReportApiProvider: Provider<Shake2ReportApi>
) : BluetoothGattServiceWrapper, FlipperInformationApi, LogTagProvider {
    override val TAG = "FlipperInformationApi"
    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private val infoCharacteristics = mutableMapOf<UUID, BluetoothGattCharacteristic>()

    private val scope by scopeProvider
    private val metricApi by metricApiProvider
    private val dataStoreFirstPair by dataStoreFirstPairProvider
    private val shake2ReportApi by shake2ReportApiProvider

    init {
        informationState.onEach {
            Shake2ReportHelper.updateGattInformation(shake2ReportApi, it)
        }.launchIn(scope + FlipperDispatchers.workStealingDispatcher)
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

    override suspend fun initialize(bleManager: UnsafeBleManager) {
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
            val version = String(content)
            metricApi.reportComplexEvent(FlipperGattInfoEvent(version))
            informationState.update {
                it.copy(softwareVersion = version)
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
            onBatteryLevelReceived(data)
        }.enqueue()
        bleManager.setNotificationCallbackUnsafe(
            infoCharacteristics[Constants.BatteryService.BATTERY_LEVEL]
        ).with { _, data ->
            onBatteryLevelReceived(data)
        }
        bleManager.enableNotificationsUnsafe(
            infoCharacteristics[Constants.BatteryService.BATTERY_LEVEL]
        ).enqueue()

        bleManager.readCharacteristicUnsafe(
            infoCharacteristics[Constants.BatteryService.BATTERY_POWER_STATE]
        ).with { _, data ->
            onBatteryPowerStateReceived(data)
        }.enqueue()
        bleManager.setNotificationCallbackUnsafe(
            infoCharacteristics[Constants.BatteryService.BATTERY_POWER_STATE]
        ).with { _, data ->
            onBatteryPowerStateReceived(data)
        }
        bleManager.enableNotificationsUnsafe(
            infoCharacteristics[Constants.BatteryService.BATTERY_POWER_STATE]
        ).enqueue()
    }

    private fun onBatteryLevelReceived(data: Data) {
        val content = data.value ?: return
        val batteryLevel = content.firstOrNull() ?: return
        info { "Battery level is $batteryLevel" }
        val batteryLevelAsFloat = batteryLevel.toFloat() / MAX_BATTERY_LEVEL
        if (batteryLevelAsFloat in 0.0f..1.0f) {
            informationState.update {
                it.copy(batteryLevel = batteryLevelAsFloat)
            }
        }
    }

    private fun onBatteryPowerStateReceived(data: Data) {
        val content = data.value ?: return
        val batteryPowerState = content.firstOrNull() ?: return
        info { "Battery power state is $batteryPowerState" }
        if (batteryPowerState and Constants.BatteryService.BATTERY_POWER_STATE_MASK ==
            Constants.BatteryService.BATTERY_POWER_STATE_MASK
        ) {
            informationState.update {
                it.copy(isCharging = true)
            }
        } else {
            informationState.update {
                it.copy(isCharging = false)
            }
        }
    }

    private fun onDeviceNameReceived(deviceName: String) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            dataStoreFirstPair.updateData {
                var deviceNameFormatted = deviceName.trim()
                if (deviceNameFormatted.startsWith(Constants.DEVICENAME_PREFIX)) {
                    deviceNameFormatted = deviceNameFormatted
                        .replaceFirst(Constants.DEVICENAME_PREFIX, "")
                        .trim()
                }
                it.copy(
                    device_name = deviceNameFormatted
                )
            }
        }
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        informationState.emit(FlipperGATTInformation())
    }

    override fun getInformationFlow(): StateFlow<FlipperGATTInformation> {
        return informationState
    }
}
