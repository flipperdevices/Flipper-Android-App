package com.flipperdevices.bridge.impl.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.BuildConfig
import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.manager.FlipperReadyListener
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.delegates.FlipperConnectionInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.bridge.impl.manager.service.FlipperVersionApiImpl
import com.flipperdevices.bridge.impl.manager.service.RestartRPCApiImpl
import com.flipperdevices.bridge.impl.manager.service.request.FlipperRequestApiImpl
import com.flipperdevices.bridge.impl.manager.service.requestservice.FlipperRtcUpdateService
import com.flipperdevices.bridge.impl.utils.BridgeImplConfig.BLE_VLOG
import com.flipperdevices.bridge.impl.utils.initializeSafe
import com.flipperdevices.bridge.impl.utils.onServiceReceivedSafe
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.ConnectRequest
import no.nordicsemi.android.ble.ConnectionPriorityRequest
import javax.inject.Inject
import javax.inject.Provider

@Suppress("LongParameterList")
@SingleIn(FlipperBleServiceGraph::class)
@ContributesBinding(FlipperBleServiceGraph::class, FlipperBleManager::class)
class FlipperBleManagerImpl @Inject constructor(
    context: Context,
    private val settingsStore: DataStore<Settings>,
    private val scope: CoroutineScope,
    private val serviceErrorListener: FlipperServiceErrorListener,
    private val flipperActionNotifier: FlipperActionNotifier,
    restartRPCApiProvider: Provider<RestartRPCApiImpl>,
    informationApiProvider: Provider<FlipperInformationApiImpl>,
    flipperRequestApiProvider: Provider<FlipperRequestApiImpl>,
    flipperVersionApiProvider: Provider<FlipperVersionApiImpl>,
    private val flipperReadyListenersProvider: Provider<Set<FlipperReadyListener>>
) : UnsafeBleManager(scope, context), FlipperBleManager, LogTagProvider {
    override val TAG = "FlipperBleManager"
    private val bleMutex = Mutex()

    private val flipperReadyListeners by flipperReadyListenersProvider

    // Store a connection request to cancel it during a disconnection
    private var connectRequest: ConnectRequest? = null

    // Gatt Delegates
    override val restartRPCApi by restartRPCApiProvider
    override val informationApi by informationApiProvider
    override val flipperRequestApi by flipperRequestApiProvider
    override val flipperVersionApi by flipperVersionApiProvider

    // RPC services
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
        flipperActionNotifier.notifyAboutAction()
        if (BLE_VLOG) {
            info { "From BleManager: $message" }
        } else if (priority >= Log.WARN) {
            warn { "From BleManager: $message" }
        }
    }

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
            flipperRtcUpdateService.initialize(flipperRequestApi)
        }.onFailure {
            error(it) { "Error while initialize RTC" }
        }

        flipperReadyListeners.forEach { listener ->
            runCatching {
                listener.onFlipperReady(scope)
            }.onFailure { error ->
                error(error) { "Failed notify flipper ready listener" }
            }
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
                    if (settingsStore.data.first().ignore_unsupported_version) {
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
        restartRPCApi.reset(this@FlipperBleManagerImpl)
        info { "RestartRPCApi reset done" }
    }
}
