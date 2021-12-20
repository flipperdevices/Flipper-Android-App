package com.flipperdevices.bridge.service.api.provider

import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.service.api.FlipperServiceApi

interface FlipperBleServiceConsumer {
    /**
     * Can be call twice or more
     */
    fun onServiceApiReady(serviceApi: FlipperServiceApi)

    /**
     * Called if the service throws an error
     */
    fun onServiceBleError(error: FlipperBleServiceError) = Unit
}
