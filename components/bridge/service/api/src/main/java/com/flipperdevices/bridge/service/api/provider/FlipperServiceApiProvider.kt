package com.flipperdevices.bridge.service.api.provider

import androidx.lifecycle.Lifecycle

interface FlipperServiceApiProvider {
    /**
     * ATTENTION:
     * Once you no longer need the BLE, call {@link FlipperServiceApi#disconnect(FlipperBleServiceConsumer)}
     *
     * @param consumer store as WeakReference
     * @param onDestroyEvent on which lifecycle event we destroy connection
     * @return instance for communicate with ble
     */
    fun provideServiceApi(
        consumer: FlipperBleServiceConsumer,
        onDestroyEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
    )
}
