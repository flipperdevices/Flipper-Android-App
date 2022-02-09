package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.flipperdevices.bridge.api.manager.ktx.providers.ConnectionStateProvider
import com.flipperdevices.bridge.api.manager.observers.ConnectionObserverComposite
import com.flipperdevices.bridge.api.manager.observers.ConnectionObserverLogger
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.observer.ConnectionObserver

/**
 * Class for initial connection to device. Set up connection and set up pairing
 */
internal class FirstPairBleManager(
    context: Context
) : BleManager(context),
    LogTagProvider,
    ConnectionStateProvider {
    override val TAG = "FirstPairBleManager"

    private val connectionObservers = ConnectionObserverComposite(ConnectionObserverLogger(TAG))

    init {
        setConnectionObserver(connectionObservers)
    }

    override fun getGattCallback(): BleManagerGattCallback = FirstPairBleManagerGattCallback()

    private inner class FirstPairBleManagerGattCallback :
        BleManagerGattCallback() {
        private var informationService: BluetoothGattService? = null

        override fun initialize() {
            info { "Initialize device" }
            if (!isBonded) {
                info { "Start bond secure" }
                ensureBond().enqueue()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onDeviceReady() {
            super.onDeviceReady()
            info { "On device ready called" }
            readCharacteristic(
                informationService?.getCharacteristic(
                    Constants.BLEInformationService.SOFTWARE_VERSION
                )
            ).with { device, data ->
                val softwareVersion = String(data.value ?: byteArrayOf())
                info { "Software version on ${device.name} is $softwareVersion" }
            }.enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            informationService = gatt.getService(Constants.BLEInformationService.SERVICE_UUID)
            return informationService != null
        }

        override fun onServicesInvalidated() = Unit
    }

    fun connectToDevice(device: BluetoothDevice) {
        connect(device).enqueue()
    }

    override fun subscribeOnConnectionState(observer: ConnectionObserver) {
        connectionObservers.addObserver(observer)
        setConnectionObserver(connectionObservers)
    }

    override fun unsubscribeConnectionState(observer: ConnectionObserver) {
        connectionObservers.removeObserver(observer)
        setConnectionObserver(connectionObservers)
    }
}
