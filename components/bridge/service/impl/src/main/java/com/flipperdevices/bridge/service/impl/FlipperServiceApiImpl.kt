package com.flipperdevices.bridge.service.impl

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.delegate.FlipperSafeConnectWrapper
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Provider

@SingleIn(FlipperBleServiceGraph::class)
@ContributesBinding(FlipperBleServiceGraph::class, FlipperServiceApi::class)
class FlipperServiceApiImpl @Inject constructor(
    scopeProvider: Provider<CoroutineScope>,
    pairSettingsStoreProvider: Provider<DataStore<PairSettings>>,
    bleManagerProvider: Provider<FlipperBleManager>,
    flipperSafeConnectWrapperProvider: Provider<FlipperSafeConnectWrapper>
) : FlipperServiceApi, LogTagProvider {
    override val TAG = "FlipperServiceApi"

    private val scope by scopeProvider
    private val pairSettingsStore by pairSettingsStoreProvider
    private val bleManager by bleManagerProvider
    private val flipperSafeConnectWrapper by flipperSafeConnectWrapperProvider

    private val inited = AtomicBoolean(false)
    private val mutex = Mutex()
    private var disconnectForced = false

    override val connectionInformationApi = bleManager.connectionInformationApi
    override val requestApi = bleManager.flipperRequestApi
    override val flipperInformationApi = bleManager.informationApi
    override val flipperVersionApi = bleManager.flipperVersionApi

    fun internalInit() {
        if (!inited.compareAndSet(false, true)) {
            error { "Service api already inited" }
            return
        }
        info { "Internal init and try connect" }

        var deviceId: String? = null
        scope.launch(Dispatchers.Default) {
            pairSettingsStore.data.collectLatest {
                withLock(mutex, "connect") {
                    if (it.deviceId != deviceId) {
                        deviceId = it.deviceId
                        flipperSafeConnectWrapper.onActiveDeviceUpdate(deviceId)
                    }
                }
            }
        }
    }

    override fun connectIfNotForceDisconnect() = launchWithLock(mutex, scope, "connect_soft") {
        if (disconnectForced) {
            return@launchWithLock
        }
        if (bleManager.isConnected() || flipperSafeConnectWrapper.isTryingConnected()) {
            return@launchWithLock
        }
        val deviceId = pairSettingsStore.data.first().deviceId
        flipperSafeConnectWrapper.onActiveDeviceUpdate(deviceId)
    }

    override suspend fun disconnect(isForce: Boolean) = withLock(mutex, "disconnect") {
        if (isForce) {
            disconnectForced = true
        }
        flipperSafeConnectWrapper.onActiveDeviceUpdate(null)
    }

    override suspend fun reconnect() = withLock(mutex, "reconnect") {
        val deviceId = pairSettingsStore.data.first().deviceId
        flipperSafeConnectWrapper.onActiveDeviceUpdate(deviceId)
    }

    suspend fun close() = withLock(mutex, "close") {
        disconnect()
        info { "Disconnect successful, close manager" }
        bleManager.close()
    }

    override suspend fun restartRPC() {
        bleManager.restartRPCApi.restartRpc()
    }
}
