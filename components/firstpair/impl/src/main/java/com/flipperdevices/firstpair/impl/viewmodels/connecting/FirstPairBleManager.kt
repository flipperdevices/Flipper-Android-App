package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.flipperdevices.bridge.api.manager.ktx.providers.ConnectionStateProvider
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.manager.observers.ConnectionObserverComposite
import com.flipperdevices.bridge.api.manager.observers.ConnectionObserverLogger
import com.flipperdevices.bridge.api.manager.observers.SuspendConnectionObserver
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.BleManager

/**
 * Class for initial connection to device. Set up connection and set up pairing
 */
@Suppress("BlockingMethodInNonBlockingContext")
internal class FirstPairBleManager(
    context: Context,
    scope: CoroutineScope
) : BleManager(context),
    LogTagProvider,
    ConnectionStateProvider {
    override val TAG = "FirstPairBleManager"

    private val connectionObservers = ConnectionObserverComposite(
        scope, ConnectionObserverLogger(TAG)
    )
    private val bleMutex = Mutex()

    init {
        setConnectionObserver(connectionObservers)
    }

    override fun supportState() = FlipperSupportedState.READY

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

    suspend fun connectToDevice(device: BluetoothDevice) = withLock(bleMutex, "connect") {
        connect(device)
            .useAutoConnect(false)
            .enqueue()

        // Wait until device is really connected
        stateAsFlow().filter { it is ConnectionState.Initializing }.first()
    }

    override fun subscribeOnConnectionState(observer: SuspendConnectionObserver) {
        connectionObservers.addObserver(observer)
        setConnectionObserver(connectionObservers)
    }

    override fun unsubscribeConnectionState(observer: SuspendConnectionObserver) {
        connectionObservers.removeObserver(observer)
        setConnectionObserver(connectionObservers)
    }
}
