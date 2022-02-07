package com.flipperdevices.bridge.impl.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import com.flipperdevices.bridge.api.manager.ktx.providers.BondStateProvider
import com.flipperdevices.bridge.api.manager.ktx.providers.ConnectionStateProvider
import com.flipperdevices.bridge.impl.manager.observers.BondingObserverComposite
import com.flipperdevices.bridge.impl.manager.observers.ConnectionObserverComposite
import com.flipperdevices.bridge.impl.manager.observers.ConnectionObserverLogger
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.observer.BondingObserver
import no.nordicsemi.android.ble.observer.ConnectionObserver

/**
 * BleManager from nordic library use protected method
 * So we can't call it outside BleManager
 * And the BleManager becomes very big
 * This wrapper allows you to call protected methods to delegates
 */
abstract class UnsafeBleManager(
    context: Context
) : BleManager(context), ConnectionStateProvider, BondStateProvider {
    private val connectionObservers = ConnectionObserverComposite(ConnectionObserverLogger(TAG))
    private val bondingObservers = BondingObserverComposite()

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

    override fun subscribeOnConnectionState(observer: ConnectionObserver) {
        connectionObservers.addObserver(observer)
        setConnectionObserver(connectionObservers)
    }

    override fun unsubscribeConnectionState(observer: ConnectionObserver) {
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
}
