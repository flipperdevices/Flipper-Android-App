package com.flipperdevices.bridge.connection.feature.protocolversion.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.core.data.SemVer
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val VERSION_WAITING_TIMEOUT = 30.seconds

interface FVersionFeatureApi : FDeviceFeatureApi {
    fun getVersionInformationFlow(): StateFlow<SemVer?>

    suspend fun isSupported(
        version: SemVer,
        timeout: Duration = VERSION_WAITING_TIMEOUT
    ): Boolean

    fun interface Factory : FDeviceFeatureApi.Factory
}