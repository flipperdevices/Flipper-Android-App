package com.flipperdevices.bridge.service.impl.provider.error

import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener

interface CompositeFlipperServiceErrorListener {
    fun subscribe(errorListener: FlipperServiceErrorListener)
    fun unsubscribe(errorListener: FlipperServiceErrorListener)
}
