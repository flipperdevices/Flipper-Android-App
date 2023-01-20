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
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.bridge.api.manager.delegates.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.delegates.FlipperConnectionInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperVersionApiImpl
import com.flipperdevices.bridge.impl.manager.service.RestartRPCApiImpl
import com.flipperdevices.bridge.impl.manager.service.request.FlipperRequestApiImpl
import com.flipperdevices.bridge.impl.manager.service.requestservice.FlipperRpcInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.requestservice.FlipperRtcUpdateService
import com.flipperdevices.bridge.impl.utils.initializeSafe
import com.flipperdevices.bridge.impl.utils.onServiceReceivedSafe
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.ConnectRequest
import no.nordicsemi.android.ble.ConnectionPriorityRequest

@Suppress("BlockingMethodInNonBlockingContext", "LongParameterList")
class FlipperBleManagerImpl(
    context: Context,
    private val settingsStore: DataStore<Settings>,
    dataStore: DataStore<PairSettings>,
    private val scope: CoroutineScope,
    private val serviceErrorListener: FlipperServiceErrorListener,
    flipperLagsDetector: FlipperLagsDetector,
    private val flipperActionNotifier: FlipperActionNotifier,
    sentryApi: Shake2ReportApi,
    metricApi: MetricApi
) : UnsafeBleManager(scope, context), FlipperBleManager, LogTagProvider {
    override val TAG = "FlipperBleManager"
    private val bleMutex = Mutex()

    // Store a connection request to cancel it during a disconnection
    private var connectRequest: ConnectRequest? = null

    // Gatt Delegates
    override val restartRPCApi = RestartRPCApiImpl()
    override val informationApi = FlipperInformationApiImpl(scope, metricApi)
    override val flipperRequestApi = FlipperRequestApiImpl(
        scope,
        flipperActionNotifier,
        flipperLagsDetector,
        restartRPCApi,
        sentryApi
    )
    override val flipperVersionApi = FlipperVersionApiImpl(settingsStore)

    // RPC services
    override val flipperRpcInformationApi = FlipperRpcInformationApiImpl(
        scope,
        metricApi,
        dataStore
    )
    private val flipperRtcUpdateService = FlipperRtcUpdateService()

    // Manager delegates
    override val connectionInformationApi = FlipperConnectionInformationApiImpl(this)

    init {
        info { "FlipperBleManagerImpl: ${this.hashCode()}" }
    }

    override suspend fun disconnectDevice() {
        withLock(bleMutex, "disconnect") {
            // Deleting connection requests
            connectRequest?.cancelPendingConnection()
            disconnect().enqueue()
            return@withLock
        }

        // Wait until device is really disconnected
        stateAsFlow().filter { it is ConnectionState.Disconnected }.first()
    }

    override suspend fun connectToDevice(device: BluetoothDevice) {
        withLock(bleMutex, "connect") {
            val connectRequestLocal = connect(device).retry(
                Constants.BLE.RECONNECT_COUNT,
                Constants.BLE.RECONNECT_TIME_MS.toInt()
            ).useAutoConnect(true)

            connectRequestLocal.enqueue()

            connectRequest = connectRequestLocal

            return@withLock
        }

        // Wait until device is really connected
        stateAsFlow().filter { it is ConnectionState.Initializing }.first()
    }

    override fun log(priority: Int, message: String) {
        info { "From BleManager: $message" }
    }

    override fun getGattCallback(): BleManagerGattCallback = FlipperBleManagerGattCallback()

    private inner class FlipperBleManagerGattCallback :
        BleManagerGattCallback() {

        override fun initialize() {
            if (!isBonded) {
                info { "Start bond secure" }
                ensureBond().enqueue()
            }
        }

        override fun onDeviceReady() = launchWithLock(bleMutex, scope, "init") {
            // Set up large MTU
            // Also does not work with small MTU because of a bug in Flipper Zero firmware
            requestMtu(Constants.BLE.MAX_MTU).enqueue()
            requestConnectionPriority(
                ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH
            ).enqueue()

            info { "On device ready called" }
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
                serviceErrorListener.onError(
                    FlipperBleServiceError.SERVICE_SERIAL_FAILED_INIT
                )
            }
            restartRPCApi.initializeSafe(this@FlipperBleManagerImpl) {
                error(it) { "Error while initialize restart api" }
                serviceErrorListener.onError(
                    FlipperBleServiceError.SERVICE_SERIAL_FAILED_INIT
                )
            }
            runCatching {
                flipperRpcInformationApi.initialize(flipperRequestApi)
            }.onFailure {
                error(it) { "Error while initialize rpc information api" }
            }
            runCatching {
                flipperRtcUpdateService.initialize(flipperRequestApi)
            }.onFailure {
                error(it) { "Error while initialize RTC" }
            }

            return@launchWithLock
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
                    runBlockingWithLog {
                        if (settingsStore.data.first().ignoreUnsupportedVersion) {
                            FlipperSupportedState.READY
                        } else {
                            FlipperSupportedState.DEPRECATED_FLIPPER
                        }
                    }
                )
                serviceErrorListener.onError(FlipperBleServiceError.SERVICE_VERSION_NOT_FOUND)
            }
            restartRPCApi.onServiceReceivedSafe(gatt) {
                error(it) { "Can't find service for restart api" }
            }

            return true
        }

        override fun onServicesInvalidated() = launchWithLock(bleMutex, scope, "reset") {
            informationApi.reset(this@FlipperBleManagerImpl)
            info { "Information api reset done" }
            flipperVersionApi.reset(this@FlipperBleManagerImpl)
            info { "FlipperVersionApi reset done" }
            flipperRequestApi.reset(this@FlipperBleManagerImpl)
            info { "FlipperRequestApi reset done" }
            flipperRpcInformationApi.reset()
            info { "FlipperRpcInformationApi reset done" }
            restartRPCApi.reset(this@FlipperBleManagerImpl)
            info { "RestartRPCApi reset done" }
        }

        @Deprecated("Use {@link ReadRequest#with(DataReceivedCallback)} instead")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // This callback is used to know that the flipper is not hanging.
            @Suppress("DEPRECATION")
            super.onCharacteristicRead(gatt, characteristic)
            flipperActionNotifier.notifyAboutAction()
        }
    }
}
