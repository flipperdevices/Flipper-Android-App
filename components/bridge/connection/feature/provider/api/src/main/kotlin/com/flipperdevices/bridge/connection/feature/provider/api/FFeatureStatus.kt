package com.flipperdevices.bridge.connection.feature.provider.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

sealed class FFeatureStatus<T : FDeviceFeatureApi> {
    class Supported<T : FDeviceFeatureApi>(
        val featureApi: T
    ) : FFeatureStatus<T>()

    class Unsupported<T : FDeviceFeatureApi>() : FFeatureStatus<T>()

    class Retrieving<T : FDeviceFeatureApi>() : FFeatureStatus<T>()

    class NotFound<T : FDeviceFeatureApi>() : FFeatureStatus<T>()
}
