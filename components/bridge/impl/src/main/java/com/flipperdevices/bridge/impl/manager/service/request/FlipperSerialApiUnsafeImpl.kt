package com.flipperdevices.bridge.impl.manager.service.request

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper
import com.flipperdevices.bridge.impl.manager.service.getCharacteristicOrLog
import com.flipperdevices.bridge.impl.manager.service.getServiceOrLog
import com.flipperdevices.bridge.impl.utils.BridgeImplConfig.BLE_VLOG
import com.flipperdevices.bridge.impl.utils.SpeedMeter
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class FlipperSerialApiUnsafeImpl(
    private val scope: CoroutineScope,
    private val flipperActionNotifier: FlipperActionNotifier
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
    private val speedFlowState = MutableStateFlow(FlipperSerialSpeed())

    init {
        combine(
            rxSpeed.getSpeed(),
            txSpeed.getSpeed()
        ) { rxBPS, txBPS ->
            speedFlowState.emit(
                FlipperSerialSpeed(receiveBytesInSec = rxBPS, transmitBytesInSec = txBPS)
            )
        }.launchIn(scope)
    }

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        val service = getServiceOrLog(
            gatt = gatt,
            uuid = Constants.BLESerialService.SERVICE_UUID
        ) ?: return false

        serialTxCharacteristic = getCharacteristicOrLog(service, Constants.BLESerialService.TX)
        serialRxCharacteristic = getCharacteristicOrLog(service, Constants.BLESerialService.RX)

        return serialTxCharacteristic != null && serialRxCharacteristic != null
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        bleManagerInternal = bleManager
        bleManager.setNotificationCallbackUnsafe(serialRxCharacteristic).with { _, data ->
            if (BLE_VLOG) {
                info { "Receive serial data ${data.value?.size}" }
            }
            val bytes = data.value ?: return@with
            rxSpeed.onReceiveBytes(bytes.size)
            scope.launch(FlipperDispatchers.workStealingDispatcher) {
                receiveBytesFlow.emit(bytes)
            }
        }
        bleManager.enableNotificationsUnsafe(serialRxCharacteristic).enqueue()
        bleManager.enableIndicationsUnsafe(serialRxCharacteristic).enqueue()
        pendingBytes.forEach { data ->
            bleManager.writeCharacteristicUnsafe(serialTxCharacteristic, data)
                .done {
                    flipperActionNotifier.notifyAboutAction()
                    txSpeed.onReceiveBytes(data.size)
                }.enqueue()
        }
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        // Not exist states in this api
    }

    override fun receiveBytesFlow() = receiveBytesFlow
    override suspend fun getSpeed() = speedFlowState.asStateFlow()

    override fun sendBytes(data: ByteArray) {
        if (data.isEmpty()) {
            return
        }
        verbose { "Send bytes to flipper with size: ${data.size}" }
        val bleManager = bleManagerInternal
        if (bleManager == null) {
            pendingBytes.add(data)
            return
        }
        bleManager.writeCharacteristicUnsafe(serialTxCharacteristic, data)
            .split()
            .done {
                flipperActionNotifier.notifyAboutAction()
                txSpeed.onReceiveBytes(data.size)
            }
            .enqueue()
    }
}
