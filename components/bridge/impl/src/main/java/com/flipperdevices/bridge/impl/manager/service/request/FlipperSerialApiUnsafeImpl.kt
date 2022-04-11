package com.flipperdevices.bridge.impl.manager.service.request

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.flipperdevices.bridge.api.manager.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper
import com.flipperdevices.bridge.impl.manager.service.getCharacteristicOrLog
import com.flipperdevices.bridge.impl.manager.service.getServiceOrLog
import com.flipperdevices.bridge.impl.utils.SpeedMeter
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlipperSerialApiUnsafeImpl(
    private val scope: CoroutineScope,
    private val lagsDetector: FlipperLagsDetector
) : FlipperSerialApi, BluetoothGattServiceWrapper, LogTagProvider {
    override val TAG = "FlipperSerialApi"
    private val receiveBytesFlow = MutableSharedFlow<ByteArray>()

    // Store bytes which pending for sending to Flipper Zero device
    private val pendingBytes = mutableListOf<ByteArray>()

    private var bleManagerInternal: UnsafeBleManager? = null

    private var serialTxCharacteristic: BluetoothGattCharacteristic? = null
    private var serialRxCharacteristic: BluetoothGattCharacteristic? = null

    private val txSpeed = SpeedMeter(scope)
    private val rxSpeed = SpeedMeter(scope)

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        val service = getServiceOrLog(
            gatt, Constants.BLESerialService.SERVICE_UUID
        ) ?: return false

        serialTxCharacteristic = getCharacteristicOrLog(service, Constants.BLESerialService.TX)
        serialRxCharacteristic = getCharacteristicOrLog(service, Constants.BLESerialService.RX)

        return serialTxCharacteristic != null && serialRxCharacteristic != null
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        bleManagerInternal = bleManager
        bleManager.setNotificationCallbackUnsafe(serialRxCharacteristic).with { _, data ->
            info { "Receive serial data ${data.value?.size}" }
            val bytes = data.value ?: return@with
            rxSpeed.onReceiveBytes(bytes.size)
            scope.launch {
                receiveBytesFlow.emit(bytes)
            }
        }
        bleManager.enableNotificationsUnsafe(serialRxCharacteristic).enqueue()
        bleManager.enableIndicationsUnsafe(serialRxCharacteristic).enqueue()
        pendingBytes.forEach { data ->
            bleManager.writeCharacteristicUnsafe(serialTxCharacteristic, data)
                .done {
                    lagsDetector.notifyAboutAction()
                    txSpeed.onReceiveBytes(data.size)
                }.enqueue()
        }
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        // Not exist states in this api
    }

    override fun receiveBytesFlow() = receiveBytesFlow
    override suspend fun getSpeed() = rxSpeed.getSpeed()
        .combine(txSpeed.getSpeed()) { rxBPS, txBPS ->
            FlipperSerialSpeed(receiveBytesInSec = rxBPS, transmitBytesInSec = txBPS)
        }.stateIn(scope)

    override fun sendBytes(data: ByteArray) {
        if (data.isEmpty()) {
            return
        }
        info { "Send bytes to flipper with size: ${data.size}" }
        val bleManager = bleManagerInternal
        if (bleManager == null) {
            pendingBytes.add(data)
            return
        }
        bleManager.writeCharacteristicUnsafe(serialTxCharacteristic, data)
            .split()
            .done {
                lagsDetector.notifyAboutAction()
                txSpeed.onReceiveBytes(data.size)
            }
            .enqueue()
    }
}
