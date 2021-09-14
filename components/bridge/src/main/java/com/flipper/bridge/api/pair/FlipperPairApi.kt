package com.flipper.bridge.api.pair

import android.content.Context
import com.flipper.bridge.api.device.FlipperDeviceApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

interface FlipperPairApi {
    @Throws(
        SecurityException::class,
        BluetoothDisabledException::class,
        TimeoutCancellationException::class
    )
    @ExperimentalCoroutinesApi
    suspend fun connect(
        context: Context,
        deviceId: String
    ): FlipperDeviceApi
}
