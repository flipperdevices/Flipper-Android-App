package com.flipperdevices.bridge.connection.feature.protocolversion.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.core.data.SemVer

interface FSdkVersionFeatureApi : FDeviceFeatureApi {
    suspend fun getSdkVersion(): Result<SemVer>
}
