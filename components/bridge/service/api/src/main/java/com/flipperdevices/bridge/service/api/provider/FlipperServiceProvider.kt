package com.flipperdevices.bridge.service.api.provider

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
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
    )

    fun provideServiceApi(
        lifecycleOwner: LifecycleOwner,
        onError: (FlipperBleServiceError) -> Unit = {},
        onBleManager: (FlipperServiceApi) -> Unit
    )

    suspend fun getServiceApi(): FlipperServiceApi
}
