package com.flipperdevices.bridge.service.impl

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.impl.manager.FlipperBleManagerImpl
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.delegate.FlipperServiceConnectDelegate
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

class FlipperServiceApiImpl(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val serviceErrorListener: FlipperServiceErrorListener
) : FlipperServiceApi, LogTagProvider {
    override val TAG = "FlipperServiceApi"

    @Inject
    lateinit var pairSettingsStore: DataStore<PairSettings>

    private val scope = lifecycleOwner.lifecycleScope
    private val dispatcher = Dispatchers.Default.limitedParallelism(1)
    private val bleManager: FlipperBleManager = FlipperBleManagerImpl(
        context, scope, serviceErrorListener
    )
    private val connectDelegate = FlipperServiceConnectDelegate(bleManager, context)
    private val inited = AtomicBoolean(false)

    override val connectionInformationApi = bleManager.connectionInformationApi
    override val requestApi = bleManager.flipperRequestApi
    override val flipperInformationApi = bleManager.informationApi

    init {
        ComponentHolder.component<FlipperServiceComponent>().inject(this)
    }

    fun internalInit() {
        if (!inited.compareAndSet(false, true)) {
            error { "Service api already inited" }
            return
        }
        info { "Internal init and try connect" }
        var deviceId: String? = null
        pairSettingsStore.data.onEach {
            if (it.deviceId != deviceId) {
                deviceId = it.deviceId
                connectToDeviceOnStartup(deviceId ?: "")
            }
        }.launchIn(scope)
    }

    override suspend fun reconnect(): Unit = withContext(dispatcher) {
        val deviceId = pairSettingsStore.data.first().deviceId
        reconnect(deviceId)
    }

    override suspend fun reconnect(deviceId: String) = withContext(dispatcher) {
        info { "Reconnect to device $deviceId" }
        connectDelegate.reconnect(deviceId)
    }

    override suspend fun reconnect(device: BluetoothDevice) = withContext(dispatcher) {
        info { "Reconnect to device ${device.address}" }
        connectDelegate.reconnect(device)
    }

    suspend fun close() = withContext(dispatcher) {
        info { "Disconnect successful, close manager" }
        bleManager.close()
    }

    private suspend fun connectToDeviceOnStartup(deviceId: String) = withContext(dispatcher) {
        if (deviceId.isBlank()) {
            error { "Flipper id not found in storage" }
            connectDelegate.disconnect()
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_DEVICE_NOT_STORED)
            return@withContext
        }

        try {
            reconnect(deviceId)
        } catch (securityException: SecurityException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION)
            error(securityException) { "On initial connect to device" }
        } catch (bleDisabled: BluetoothDisabledException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED)
            error(bleDisabled) { "On initial connect to device" }
        } catch (timeout: TimeoutCancellationException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_TIMEOUT)
            error(timeout) { "On initial connect to device" }
        } catch (illegalArgumentException: IllegalArgumentException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_REQUIRE_REBOUND)
            error(illegalArgumentException) { "On initial connect to device" }
        }
    }
}
