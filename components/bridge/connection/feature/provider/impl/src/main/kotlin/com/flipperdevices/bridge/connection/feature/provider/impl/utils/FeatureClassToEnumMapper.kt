package com.flipperdevices.bridge.connection.feature.provider.impl.utils

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlin.reflect.KClass

object FeatureClassToEnumMapper {
    private val classToEnumMap: ImmutableMap<KClass<out FDeviceFeatureApi>, FDeviceFeature> =
        FDeviceFeature.entries.associateBy { featureEnumToClass(it) }.toPersistentMap()

    private fun featureEnumToClass(feature: FDeviceFeature): KClass<out FDeviceFeatureApi> {
        return when (feature) {
            FDeviceFeature.RPC -> FRpcFeatureApi::class
        }
    }

    fun get(clazz: KClass<out FDeviceFeatureApi>): FDeviceFeature? {
        return classToEnumMap[clazz]
    }
}
