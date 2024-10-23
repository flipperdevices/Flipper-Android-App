package com.flipperdevices.bridge.connection.device.fzero.impl.utils

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.api.FRpcInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlin.reflect.KClass

object FZeroFeatureClassToEnumMapper {
    private val classToEnumMap: ImmutableMap<KClass<out FDeviceFeatureApi>, FDeviceFeature> =
        FDeviceFeature.entries.associateBy { featureEnumToClass(it) }.toPersistentMap()

    private fun featureEnumToClass(feature: FDeviceFeature): KClass<out FDeviceFeatureApi> {
        return when (feature) {
            FDeviceFeature.RPC -> FRpcFeatureApi::class
            FDeviceFeature.SERIAL_LAGS_DETECTOR -> FLagsDetectorFeature::class
            FDeviceFeature.SERIAL_RESTART_RPC -> FRestartRpcFeatureApi::class
            FDeviceFeature.SERIAL_SPEED -> FSpeedFeatureApi::class
            FDeviceFeature.VERSION -> FVersionFeatureApi::class
            FDeviceFeature.RPC_INFO -> FRpcInfoFeatureApi::class
            FDeviceFeature.STORAGE_INFO -> FStorageInfoFeatureApi::class
            FDeviceFeature.GET_INFO -> FGetInfoFeatureApi::class
            FDeviceFeature.STORAGE -> FStorageFeatureApi::class
        }
    }

    fun get(clazz: KClass<out FDeviceFeatureApi>): FDeviceFeature? {
        return classToEnumMap[clazz]
    }
}
