package com.flipperdevices.bridge.connection.feature.storageinfo.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FStorageInfoFeatureApi : FDeviceFeatureApi {
    fun getStorageInformationFlow(): StateFlow<FlipperStorageInformation>

    /**
     * Update current [getStorageInformationFlow]
     * @param scope parent scope to be launched in
     * @param force if true, will cancel  last invalidation and start new.
     * If true and invalidation in progress, will be skipped
     */
    suspend fun invalidate(
        scope: CoroutineScope,
        force: Boolean = false
    )

    /**
     * Cancels current [invalidate] and reset [getStorageInformationFlow]
     */
    suspend fun reset()
}
