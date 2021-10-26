package com.flipperdevices.bridge.service.impl.provider.error

import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceError

@FunctionalInterface
interface FlipperServiceErrorListener {
    fun onError(error: FlipperBleServiceError)
}
