package com.flipperdevices.bridge.service.impl.provider

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceError

class LambdaFlipperBleServiceConsumer(
    private val onBleManager: (FlipperServiceApi) -> Unit,
    private val onError: (FlipperBleServiceError) -> Unit
) : FlipperBleServiceConsumer {
    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        onBleManager(serviceApi)
    }

    override fun onServiceBleError(error: FlipperBleServiceError) {
        super.onServiceBleError(error)
        onError(error)
    }
}
