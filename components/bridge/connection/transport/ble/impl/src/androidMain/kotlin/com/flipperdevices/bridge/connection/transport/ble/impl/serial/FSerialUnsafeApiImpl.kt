package com.flipperdevices.bridge.connection.transport.ble.impl.serial

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.ble.impl.model.BLEConnectionPermissionException
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import com.flipperdevices.core.log.LogTagProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray

private const val DAGGER_ID_CHARACTERISTIC_RX = "rx_service"
private const val DAGGER_ID_CHARACTERISTIC_TX = "tx_service"

@OptIn(ExperimentalStdlibApi::class)
class FSerialUnsafeApiImpl @AssistedInject constructor(
    @Assisted(DAGGER_ID_CHARACTERISTIC_RX) val rxCharacteristic: ClientBleGattCharacteristic,
    @Assisted(DAGGER_ID_CHARACTERISTIC_TX) val txCharacteristic: ClientBleGattCharacteristic,
    @Assisted scope: CoroutineScope,
    @Assisted private val flipperActionNotifier: FlipperActionNotifier,
    private val context: Context,
) : FSerialDeviceApi, LogTagProvider {
    override val TAG = "FSerialUnsafeApiImpl"

    private val receiverByteFlow = MutableSharedFlow<ByteArray>()

    private val txSpeed = SpeedMeter(scope)
    private val rxSpeed = SpeedMeter(scope)
    private val speedFlowState = MutableStateFlow(FlipperSerialSpeed())

    override fun getActionNotifier() = flipperActionNotifier

    init {
        scope.launch {
            rxCharacteristic.getNotifications(
                bufferOverflow = BufferOverflow.SUSPEND
            ).collect {
                receiverByteFlow.emit(it.value)
                rxSpeed.onReceiveBytes(it.size)
            }
        }
        combine(
            rxSpeed.getSpeed(),
            txSpeed.getSpeed()
        ) { rxBPS, txBPS ->
            flipperActionNotifier.notifyAboutAction()
            speedFlowState.emit(
                FlipperSerialSpeed(receiveBytesInSec = rxBPS, transmitBytesInSec = txBPS)
            )
        }.launchIn(scope)
    }

    override suspend fun getSpeed() = speedFlowState.asStateFlow()

    override suspend fun getReceiveBytesFlow(): Flow<ByteArray> {
        return receiverByteFlow.asSharedFlow()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun sendBytes(data: ByteArray) {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw BLEConnectionPermissionException()
        }
        txCharacteristic.splitWrite(DataByteArray(data))
        txSpeed.onReceiveBytes(data.size)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted(DAGGER_ID_CHARACTERISTIC_RX) rxCharacteristic: ClientBleGattCharacteristic,
            @Assisted(DAGGER_ID_CHARACTERISTIC_TX) txCharacteristic: ClientBleGattCharacteristic,
            scope: CoroutineScope,
            flipperActionNotifier: FlipperActionNotifier
        ): FSerialUnsafeApiImpl
    }
}
