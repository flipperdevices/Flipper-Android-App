package com.flipperdevices.bridge.connection.device.fzero.impl.api

import com.flipperdevices.bridge.connection.device.fzero.api.FZeroDeviceApi
import com.flipperdevices.bridge.connection.device.fzero.impl.utils.FZeroFeatureClassToEnumMapper
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.gulya.anvil.assisted.ContributesAssistedFactory
import java.util.EnumMap
import kotlin.reflect.KClass

@ContributesAssistedFactory(AppGraph::class, FZeroDeviceApi.Factory::class)
class FZeroDeviceApiImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val connectedDevice: FConnectedDeviceApi,
    private val rpcFeatureFactory: FRpcFeatureApi.Factory,
    private val restartRpcFeatureFactory: FRestartRpcFeatureApi.Factory,
    private val serialLagsDetectorFactory: FLagsDetectorFeature.Factory,
    private val speedFeatureFactory: FSpeedFeatureApi.Factory
) : FZeroDeviceApi, FUnsafeDeviceFeatureApi, LogTagProvider {
    override val TAG = "FZeroDeviceApi"

    private val features = EnumMap<FDeviceFeature, FDeviceFeatureApi>(FDeviceFeature::class.java)
    private val mutex = Mutex()
    override suspend fun <T : FDeviceFeatureApi> get(clazz: KClass<T>): T? = mutex.withLock {
        return@withLock getUnsafe(clazz)
    }

    override fun <T : FDeviceFeatureApi> getUnsafe(clazz: KClass<T>): T? {
        val deviceFeature = FZeroFeatureClassToEnumMapper.get(clazz) ?: return null
        val featureApi = getFeatureApi(deviceFeature)
        if (!clazz.isInstance(featureApi)) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return featureApi as? T
    }

    private fun getFeatureApi(feature: FDeviceFeature): FDeviceFeatureApi? {
        var featureApi = features[feature]
        if (featureApi != null) {
            return featureApi
        }
        val factory = when (feature) {
            FDeviceFeature.RPC -> rpcFeatureFactory
            FDeviceFeature.SERIAL_LAGS_DETECTOR -> serialLagsDetectorFactory
            FDeviceFeature.SERIAL_RESTART_RPC -> restartRpcFeatureFactory
            FDeviceFeature.SERIAL_SPEED -> speedFeatureFactory
        }
        featureApi = factory(
            unsafeFeatureDeviceApi = this,
            scope = scope,
            connectedDevice = connectedDevice
        )
        features[feature] = featureApi
        return featureApi
    }
}
