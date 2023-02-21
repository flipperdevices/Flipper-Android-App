package com.flipperdevices.bridge.service.impl

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.impl.manager.FlipperBleManagerImpl
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.delegate.FlipperActionNotifierImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperAutoDisconnect
import com.flipperdevices.bridge.service.impl.delegate.FlipperLagsDetectorImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperSafeConnectWrapper
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.bridge.service.impl.utils.WeakConnectionStateProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class FlipperServiceApiImpl(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    serviceErrorListener: FlipperServiceErrorListener
) : FlipperServiceApi, LogTagProvider {
    override val TAG = "FlipperServiceApi"

    @Inject
    lateinit var pairSettingsStore: DataStore<PairSettings>

    @Inject
    lateinit var settingsStore: DataStore<Settings>

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var sentryApi: Shake2ReportApi

    @Inject
    lateinit var application: Application

    init {
        ComponentHolder.component<FlipperServiceComponent>().inject(this)
    }

    private val scope = lifecycleOwner.lifecycleScope + Dispatchers.Default
    private val connectionStateProvider = WeakConnectionStateProvider(scope)
    private val flipperActionNotifier = FlipperActionNotifierImpl(scope)
    private val flipperAutoDisconnect = FlipperAutoDisconnect(
        scope,
        flipperActionNotifier,
        this,
        application
    )
    private val lagsDetector = FlipperLagsDetectorImpl(
        scope = scope,
        serviceApi = this,
        connectionStateProvider = connectionStateProvider,
        flipperActionNotifier = flipperActionNotifier
    )
    private val bleManager: FlipperBleManager = FlipperBleManagerImpl(
        context,
        settingsStore,
        scope,
        serviceErrorListener,
        lagsDetector,
        flipperActionNotifier,
        sentryApi,
        metricApi
    ).apply {
        connectionStateProvider.initialize(this.connectionInformationApi)
    }

    private val inited = AtomicBoolean(false)
    private val mutex = Mutex()
    private val flipperSafeConnectWrapper =
        FlipperSafeConnectWrapper(context, bleManager, scope, serviceErrorListener)
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
        flipperAutoDisconnect.init()
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

    override fun restartRPC() {
        bleManager.restartRPCApi.restartRpc()
    }
}
