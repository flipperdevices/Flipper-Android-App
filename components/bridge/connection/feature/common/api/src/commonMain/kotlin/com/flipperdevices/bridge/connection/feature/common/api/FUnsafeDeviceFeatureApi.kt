package com.flipperdevices.bridge.connection.feature.common.api

import kotlin.reflect.KClass

/**
 * This is a special unsafe interface that cannot be used anywhere except inside the FDeviceFeatureApiFactory.
 * The operations that are performed in it are not thread-safe,
 * which means that the caller must ensure that no more than one call at a time are made to the methods
 */
interface FUnsafeDeviceFeatureApi {
    suspend fun <T : FDeviceFeatureApi> getUnsafe(clazz: KClass<T>): T?
}

suspend inline fun <reified T : FDeviceFeatureApi> FUnsafeDeviceFeatureApi.getUnsafe(): T? {
    return getUnsafe(T::class)
}
