package com.flipperdevices.bridge.connection.feature.storageinfo.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FStorageInfoFeatureApi : FDeviceFeatureApi {
    fun getStorageInformationFlow(): StateFlow<FlipperStorageInformation>

    suspend fun invalidate(
        scope: CoroutineScope,
        force: Boolean = false
    )

    suspend fun reset()
}
