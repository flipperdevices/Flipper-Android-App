package com.flipperdevices.bridge.connection.feature.getinfo.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiGroup
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import kotlinx.coroutines.flow.Flow

interface FGetInfoFeatureApi : FDeviceFeatureApi {
    suspend fun get(property: FGetInfoApiProperty): Result<String>

    fun get(group: FGetInfoApiGroup): Flow<Pair<FGetInfoApiProperty, String>>
}
