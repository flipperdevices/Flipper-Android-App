package com.flipperdevices.bridge.connection.feature.provider.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface FFeatureProvider {
    fun <T : FDeviceFeatureApi> get(clazz: KClass<T>): Flow<FFeatureStatus<T>>

    suspend fun <T : FDeviceFeatureApi> getSync(clazz: KClass<T>): T?
}

inline fun <reified T : FDeviceFeatureApi> FFeatureProvider.get(): Flow<FFeatureStatus<T>> {
    return get(T::class)
}

suspend inline fun <reified T : FDeviceFeatureApi> FFeatureProvider.getSync(): T? {
    return getSync(T::class)
}
