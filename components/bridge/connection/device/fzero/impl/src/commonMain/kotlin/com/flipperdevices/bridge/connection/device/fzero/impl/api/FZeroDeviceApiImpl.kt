package com.flipperdevices.bridge.connection.device.fzero.impl.api

import com.flipperdevices.bridge.connection.device.fzero.api.FZeroDeviceApi
import com.flipperdevices.bridge.connection.device.fzero.impl.utils.FZeroFeatureClassToEnumMapper
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FOnDeviceReadyFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.gulya.anvil.assisted.ContributesAssistedFactory
import java.util.EnumMap
import kotlin.reflect.KClass

@ContributesAssistedFactory(AppGraph::class, FZeroDeviceApi.Factory::class)
class FZeroDeviceApiImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val connectedDevice: FConnectedDeviceApi,
    onReadyFeaturesApiFactories: MutableSet<FOnDeviceReadyFeatureApi.Factory>,
    private val factories: MutableMap<FDeviceFeature, FDeviceFeatureApi.Factory>
) : FZeroDeviceApi, FUnsafeDeviceFeatureApi, LogTagProvider {
    override val TAG = "FZeroDeviceApi"

    private val features = EnumMap<FDeviceFeature, FDeviceFeatureApi>(FDeviceFeature::class.java)
    private val mutex = Mutex()

    init {
        if (BuildKonfig.CRASH_APP_ON_FAILED_CHECKS) {
            FDeviceFeature.entries.forEach { key ->
                checkNotNull(factories[key]) { "Not found factory for $key" }
            }
        }

        scope.launch {
            callAllOnReadyDeviceFeatures(onReadyFeaturesApiFactories)
        }
    }

    override suspend fun <T : FDeviceFeatureApi> get(clazz: KClass<T>): T? = mutex.withLock {
        return@withLock getUnsafe(clazz)
    }

    override suspend fun <T : FDeviceFeatureApi> getUnsafe(clazz: KClass<T>): T? {
        val deviceFeature = FZeroFeatureClassToEnumMapper.get(clazz) ?: return null
        val featureApi = getFeatureApi(deviceFeature)
        if (!clazz.isInstance(featureApi)) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return featureApi as? T
    }

    private suspend fun getFeatureApi(feature: FDeviceFeature): FDeviceFeatureApi? {
        var featureApi = features[feature]
        if (featureApi != null) {
            return featureApi
        }
        val factory = factories[feature]
        if (factory == null) {
            error { "Fail to find factory for feature $feature" }
            return null
        }
        info { "$feature feature start creation..." }
        featureApi = factory(
            unsafeFeatureDeviceApi = this,
            scope = scope,
            connectedDevice = connectedDevice
        )
        features[feature] = featureApi
        info { "$feature feature creation successful!" }
        return featureApi
    }

    private suspend fun callAllOnReadyDeviceFeatures(
        factories: Set<FOnDeviceReadyFeatureApi.Factory>
    ) = mutex.withLock {
        for (factory in factories) {
            try {
                val featureApi = factory(
                    unsafeFeatureDeviceApi = this,
                    scope = scope,
                    connectedDevice = connectedDevice
                )
                featureApi?.onReady()
            } catch (e: Throwable) {
                error(e) { "Failed init on ready device factory $factory" }
            }
        }
    }
}
