package com.flipperdevices.bridge.api.error

@FunctionalInterface
interface FlipperServiceErrorListener {
    fun onError(error: FlipperBleServiceError)
}
