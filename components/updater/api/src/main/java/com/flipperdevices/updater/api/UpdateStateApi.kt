package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.FlipperUpdateState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UpdateStateApi {
    fun getFlipperUpdateState(
        scope: CoroutineScope
    ): Flow<FlipperUpdateState>
}
