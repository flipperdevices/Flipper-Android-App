package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.protobuf.app.AppStateResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

const val APP_STARTED_TIMEOUT_MS = 3 * 1000L // 3 seconds

interface AppEmulateHelper {
    fun appStateFlow(): Flow<AppStateResponse>

    @Throws(AlreadyOpenedAppException::class)
    suspend fun tryOpenApp(
        scope: CoroutineScope,
        keyType: FlipperKeyType
    ): Boolean
}
