package com.flipperdevices.bridge.connection.feature.common.api

import dagger.MapKey

enum class FDeviceFeature {
    RPC,
    SERIAL_LAGS_DETECTOR,
    SERIAL_RESTART_RPC,
    SERIAL_SPEED,
    VERSION,
    RPC_INFO,
    STORAGE,
    STORAGE_INFO,
    GET_INFO
}

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FDeviceFeatureQualifier(val enum: FDeviceFeature)
