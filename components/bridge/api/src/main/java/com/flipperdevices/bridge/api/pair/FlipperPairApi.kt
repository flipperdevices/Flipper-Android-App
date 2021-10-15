package com.flipperdevices.bridge.api.pair

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.flipperdevices.bridge.api.device.FlipperDeviceApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

interface FlipperPairApi {
    fun getFlipperApi(
        context: Context,
        deviceId: String
    ): FlipperDeviceApi

    @Throws(
        SecurityException::class,
        BluetoothDisabledException::class,
        TimeoutCancellationException::class,
        IllegalArgumentException::class
    )
    @ExperimentalCoroutinesApi
    suspend fun connect(
        context: Context,
        flipperDeviceApi: FlipperDeviceApi
    )

    /**
     * Main difference in args
     */
    fun scheduleConnect(
        flipperDeviceApi: FlipperDeviceApi,
        device: BluetoothDevice
    )
}
