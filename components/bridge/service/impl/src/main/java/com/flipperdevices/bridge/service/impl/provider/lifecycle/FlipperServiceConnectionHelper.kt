package com.flipperdevices.bridge.service.impl.provider.lifecycle

import com.flipperdevices.bridge.service.impl.FlipperServiceBinder

/**
 * Delegates for control connection with flipper service
 */
interface FlipperServiceConnectionHelper {
    val serviceBinder: FlipperServiceBinder?

    fun connect()
    fun disconnect()
}
