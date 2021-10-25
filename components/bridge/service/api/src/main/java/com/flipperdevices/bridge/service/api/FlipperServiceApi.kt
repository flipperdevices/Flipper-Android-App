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

    interface Provider {
        /**
         * ATTENTION:
         * Once you no longer need the BLE, call {@link FlipperServiceApi#disconnect(FlipperBleServiceConsumer)}
         *
         * @param consumer store as WeakReference
         * @return instance for communicate with ble
         */
        fun provideServiceApi(consumer: FlipperBleServiceConsumer)

        /**
         * Call this with the same parameter as the {@link FlipperServiceApi.Provider#provideServiceApi(FlipperBleServiceConsumer)}
         */
        fun disconnect(consumer: FlipperBleServiceConsumer)
    }
}

interface FlipperBleServiceConsumer {
    /**
     * Can be call twice or more
     */
    fun onServiceApiReady(serviceApi: FlipperServiceApi)
}
