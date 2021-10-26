package com.flipperdevices.bridge.service.api.provider

import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.service.api.FlipperServiceApi

interface FlipperBleServiceConsumer : LifecycleOwner {
    /**
     * Can be call twice or more
     */
    fun onServiceApiReady(serviceApi: FlipperServiceApi)
}
