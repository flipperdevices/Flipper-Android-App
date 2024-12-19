package com.flipperdevices.bridge.connection.feature.appstart.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi

interface FAppStartFeatureApi : FDeviceFeatureApi {
    suspend fun startApp(path: okio.Path): Result<Unit>
}
