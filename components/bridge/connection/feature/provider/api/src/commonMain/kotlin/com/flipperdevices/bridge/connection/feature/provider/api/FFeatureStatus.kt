package com.flipperdevices.bridge.connection.feature.provider.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

sealed class FFeatureStatus<out T : FDeviceFeatureApi> {
    class Supported<T : FDeviceFeatureApi>(
        val featureApi: T
    ) : FFeatureStatus<T>()

    data object Unsupported : FFeatureStatus<Nothing>()

    data object Retrieving : FFeatureStatus<Nothing>()

    data object NotFound : FFeatureStatus<Nothing>()
}
