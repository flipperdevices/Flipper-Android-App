package com.flipperdevices.bridge.service.impl.provider.error

interface CompositeFlipperServiceErrorListener {
    fun subscribe(errorListener: FlipperServiceErrorListener)
    fun unsubscribe(errorListener: FlipperServiceErrorListener)
}
