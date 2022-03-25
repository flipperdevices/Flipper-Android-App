package com.flipperdevices.bridge.impl.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipperdevices.bridge.api.manager.ktx.providers.BondStateProvider
import com.flipperdevices.bridge.api.manager.ktx.providers.ConnectionStateProvider
import com.flipperdevices.bridge.api.manager.observers.BondingObserverComposite
import com.flipperdevices.bridge.api.manager.observers.ConnectionObserverComposite
import com.flipperdevices.bridge.api.manager.observers.ConnectionObserverLogger
import com.flipperdevices.bridge.api.manager.observers.SuspendConnectionObserver
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.observer.BondingObserver

private const val UNSAFE_BLE_MANAGER_TAG = "UnsafeBleManager"

/**
 * BleManager from nordic library use protected method
 * So we can't call it outside BleManager
 * And the BleManager becomes very big
 * This wrapper allows you to call protected methods to delegates
 */
@Suppress("TooManyFunctions")
abstract class UnsafeBleManager(
    scope: CoroutineScope,
    context: Context
) : BleManager(context), ConnectionStateProvider, BondStateProvider, LogTagProvider {
    override val TAG = UNSAFE_BLE_MANAGER_TAG
    private val connectionObservers = ConnectionObserverComposite(
        scope = scope,
        ConnectionObserverLogger(UNSAFE_BLE_MANAGER_TAG)
    )
    private val bondingObservers = BondingObserverComposite()
    private var isDeviceSupported: Boolean? = null

    init {
        setConnectionObserver(connectionObservers)
        setBondingObserver(bondingObservers)
    }

    fun readCharacteristicUnsafe(characteristic: BluetoothGattCharacteristic?) =
        readCharacteristic(characteristic)

    fun writeCharacteristicUnsafe(characteristic: BluetoothGattCharacteristic?, data: ByteArray) =
        writeCharacteristic(characteristic, data)

    fun setNotificationCallbackUnsafe(characteristic: BluetoothGattCharacteristic?) =
        setNotificationCallback(characteristic)

    fun enableNotificationsUnsafe(characteristic: BluetoothGattCharacteristic?) =
        enableNotifications(characteristic)

    fun enableIndicationsUnsafe(characteristic: BluetoothGattCharacteristic?) =
        enableIndications(characteristic)

    override fun subscribeOnConnectionState(observer: SuspendConnectionObserver) {
        connectionObservers.addObserver(observer)
        setConnectionObserver(connectionObservers)
    }

    override fun unsubscribeConnectionState(observer: SuspendConnectionObserver) {
        connectionObservers.removeObserver(observer)
        setConnectionObserver(connectionObservers)
    }

    @SuppressLint("MissingPermission")
    override fun getBondState(): Int? {
        return bluetoothDevice?.bondState
    }

    override fun subscribeOnBondingState(observer: BondingObserver) {
        bondingObservers.addObserver(observer)
        setBondingObserver(bondingObservers)
    }

    override fun unsubscribeBondingState(observer: BondingObserver) {
        bondingObservers.removeObserver(observer)
        setBondingObserver(bondingObservers)
    }

    override fun isSupported() = isDeviceSupported

    fun setDeviceSupportedStatus(isDeviceSupported: Boolean) {
        this.isDeviceSupported = isDeviceSupported
        val bluetoothDeviceLocal = bluetoothDevice
        if (bluetoothDeviceLocal == null) {
            error { "Bluetooth device is null, but we already invalidate isDeviceSupported" }
            return
        }
        connectionObservers.onDeviceReady(bluetoothDeviceLocal)
    }
}
