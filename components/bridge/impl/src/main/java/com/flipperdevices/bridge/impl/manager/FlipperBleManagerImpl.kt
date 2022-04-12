package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.BuildConfig
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.manager.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.delegates.FlipperConnectionInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperVersionApiImpl
import com.flipperdevices.bridge.impl.manager.service.request.FlipperRequestApiImpl
import com.flipperdevices.bridge.impl.manager.service.requestservice.FlipperRpcInformationApiImpl
import com.flipperdevices.bridge.impl.utils.initializeSafe
import com.flipperdevices.bridge.impl.utils.onServiceReceivedSafe
import com.flipperdevices.core.ktx.jre.newSingleThreadExecutor
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.ConnectionPriorityRequest

@Suppress("BlockingMethodInNonBlockingContext")
class FlipperBleManagerImpl constructor(
    context: Context,
    private val settingsStore: DataStore<Settings>,
    private val scope: CoroutineScope,
    private val serviceErrorListener: FlipperServiceErrorListener,
    private val lagsDetector: FlipperLagsDetector
) : UnsafeBleManager(scope, context), FlipperBleManager, LogTagProvider {
    override val TAG = "FlipperBleManager"
    private val bleDispatcher = newSingleThreadExecutor(TAG)
        .asCoroutineDispatcher()

    // Gatt Delegates

    override val informationApi = FlipperInformationApiImpl(scope)
    override val flipperVersionApi = FlipperVersionApiImpl(settingsStore)
    override val flipperRequestApi = FlipperRequestApiImpl(scope, lagsDetector)

    // RPC services
    override val flipperRpcInformationApi = FlipperRpcInformationApiImpl(scope)

    // Manager delegates
    override val connectionInformationApi = FlipperConnectionInformationApiImpl(this)

    init {
        info { "FlipperBleManagerImpl: ${this.hashCode()}" }
    }

    override suspend fun disconnectDevice() = withContext(bleDispatcher) {
        disconnect().enqueue()
        // Wait until device is really disconnected
        stateAsFlow().filter { it is ConnectionState.Disconnected }.first()
        return@withContext
    }

    override suspend fun connectToDevice(device: BluetoothDevice) = withContext(bleDispatcher) {
        connect(device).retry(
            Constants.BLE.RECONNECT_COUNT,
            Constants.BLE.RECONNECT_TIME_MS.toInt()
        ).useAutoConnect(true)
            .enqueue()

        // Wait until device is really connected
        stateAsFlow().filter { it is ConnectionState.Initializing }.first()
        return@withContext
    }

    override fun log(priority: Int, message: String) {
        info { "From BleManager: $message" }
    }

    override fun getGattCallback(): BleManagerGattCallback =
        FlipperBleManagerGattCallback()

    private inner class FlipperBleManagerGattCallback :
        BleManagerGattCallback() {

        override fun initialize() {
            if (!isBonded) {
                info { "Start bond secure" }
                ensureBond().enqueue()
            }
        }

        override fun onDeviceReady() {
            scope.launch(bleDispatcher) {
                // Set up large MTU
                // Also does not work with small MTU because of a bug in Flipper Zero firmware
                requestMtu(Constants.BLE.MAX_MTU).enqueue()
                requestConnectionPriority(
                    ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH
                ).enqueue()
                informationApi.initializeSafe(this@FlipperBleManagerImpl) {
                    error(it) { "Error while initialize information api" }
                    serviceErrorListener.onError(
                        FlipperBleServiceError.SERVICE_INFORMATION_FAILED_INIT
                    )
                }
                flipperVersionApi.initializeSafe(this@FlipperBleManagerImpl) {
                    error(it) { "Error while initialize version api" }
                    serviceErrorListener.onError(
                        FlipperBleServiceError.SERVICE_VERSION_FAILED_INIT
                    )
                }
                flipperRequestApi.initializeSafe(this@FlipperBleManagerImpl) {
                    error(it) { "Error while initialize request api" }
                    serviceErrorListener.onError(FlipperBleServiceError.SERVICE_SERIAL_FAILED_INIT)
                }
                runCatching {
                    flipperRpcInformationApi.initialize(flipperRequestApi)
                }.onFailure {
                    error(it) { "Error while initialize rpc information api" }
                }
            }
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            if (BuildConfig.INTERNAL) {
                gatt.services.forEach { service ->
                    service.characteristics.forEach {
                        debug { "Characteristic for service ${service.uuid}: ${it.uuid}" }
                    }
                }
            }

            flipperRequestApi.onServiceReceivedSafe(gatt) {
                error(it) { "Can't find service for flipper request api" }
                serviceErrorListener.onError(FlipperBleServiceError.SERVICE_SERIAL_NOT_FOUND)
            }
            informationApi.onServiceReceivedSafe(gatt) {
                error(it) { "Can't find service for information api" }
                serviceErrorListener.onError(FlipperBleServiceError.SERVICE_INFORMATION_NOT_FOUND)
            }
            flipperVersionApi.onServiceReceivedSafe(gatt) {
                error(it) { "Can't find service for version api" }
                setDeviceSupportedStatus(
                    runBlocking {
                        settingsStore.data.first().ignoreUnsupportedVersion
                    }
                )
                serviceErrorListener.onError(FlipperBleServiceError.SERVICE_VERSION_NOT_FOUND)
            }

            return true
        }

        override fun onServicesInvalidated() {
            scope.launch(bleDispatcher) {
                informationApi.reset(this@FlipperBleManagerImpl)
                flipperVersionApi.reset(this@FlipperBleManagerImpl)
                flipperRequestApi.reset(this@FlipperBleManagerImpl)
                flipperRpcInformationApi.reset()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // This callback is used to know that the flipper is not hanging.
            @Suppress("DEPRECATION")
            super.onCharacteristicRead(gatt, characteristic)
            lagsDetector.notifyAboutAction()
        }
    }
}
