package com.flipperdevices.bridge.service.impl.provider.error

import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener

class CompositeFlipperServiceErrorListenerImpl :
    CompositeFlipperServiceErrorListener,
    FlipperServiceErrorListener {
    private val listeners = mutableListOf<FlipperServiceErrorListener>()

    override fun subscribe(errorListener: FlipperServiceErrorListener) {
        if (!listeners.contains(errorListener)) {
            listeners.add(errorListener)
        }
    }

    override fun unsubscribe(errorListener: FlipperServiceErrorListener) {
        listeners.remove(errorListener)
    }

    override fun onError(error: FlipperBleServiceError) {
        listeners.forEach {
            it.onError(error)
        }
    }
}
