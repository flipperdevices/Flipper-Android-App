package com.flipperdevices.bridge.connection.feature.emulate.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.model.FlipperAppError

interface FlipperAppErrorHelper {
    suspend fun requestError(): FlipperAppError
}
