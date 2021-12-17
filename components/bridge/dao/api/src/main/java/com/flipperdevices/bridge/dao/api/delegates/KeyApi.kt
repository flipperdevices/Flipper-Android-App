package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import kotlinx.coroutines.flow.Flow

interface KeyApi {
    suspend fun updateKeys(keys: List<FlipperKey>)

    fun getKeysAsFlow(
        fileType: FlipperFileType? = null
    ): Flow<List<FlipperKey>>
}
