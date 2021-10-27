package com.flipperdevices.bridge.service.api.provider

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.service.api.FlipperServiceApi

interface FlipperServiceProvider {
    /**
     * ATTENTION:
     * Once you no longer need the BLE, call {@link FlipperServiceApi#disconnect(FlipperBleServiceConsumer)}
     *
     * @param lifecycleOwner to control when we should release object
     * @param onDestroyEvent on which lifecycle event we destroy connection
     * @return instance for communicate with ble
     */
    fun provideServiceApi(
        consumer: FlipperBleServiceConsumer,
        lifecycleOwner: LifecycleOwner,
        onDestroyEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
    )

    fun provideServiceApi(
        lifecycleOwner: LifecycleOwner,
        onDestroyEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
        onError: (FlipperBleServiceError) -> Unit = {},
        onBleManager: (FlipperServiceApi) -> Unit
    )
}
