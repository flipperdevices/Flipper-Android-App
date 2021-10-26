package com.flipperdevices.bridge.service.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi

/**
 * Provides access to the API operation of the device
 * Underhood creates a service and connects to it
 */
interface FlipperServiceApi {
    /**
     * Returns an API for communicating with Flipper via a request-response structure.
     */
    fun getRequestApi(): FlipperRequestApi
}
