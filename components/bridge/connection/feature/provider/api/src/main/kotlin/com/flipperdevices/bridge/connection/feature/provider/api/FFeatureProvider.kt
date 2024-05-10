package com.flipperdevices.bridge.connection.feature.provider.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface FFeatureProvider {
    fun <T : FDeviceFeatureApi> get(clazz: KClass<T>): Flow<FFeatureStatus<T>>
}

inline fun <reified T : FDeviceFeatureApi> FFeatureProvider.get(): Flow<FFeatureStatus<T>> {
    return get(T::class)
}
