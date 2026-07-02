package com.flipperdevices.bridge.connection.transport.ble.impl.di

import android.content.Context
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.ble.impl.BleDeviceConnectionApiImpl
import com.flipperdevices.bridge.connection.transport.ble.impl.api.FBleApiWithSerialFactory
import com.flipperdevices.bridge.connection.transport.ble.impl.utils.BLEConnectionDeviceHelper
import com.flipperdevices.bridge.connection.transport.common.api.di.DeviceConnectionApiHolder
import com.flipperdevices.bridge.connection.transport.common.api.di.toHolder
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppGraph::class)
interface BleDeviceConnectionModule {

    @Provides
    @IntoMap
    @ClassKey(FBleDeviceConnectionConfig::class)
    fun provideBleDeviceConnectionApi(
        context: Context,
        bleApiWithSerialFactory: FBleApiWithSerialFactory,
        connectionHelper: BLEConnectionDeviceHelper
    ): DeviceConnectionApiHolder = BleDeviceConnectionApiImpl(
        context = context,
        bleApiWithSerialFactory = bleApiWithSerialFactory,
        connectionHelper = connectionHelper
    ).toHolder()

    @Provides
    @SingleIn(AppGraph::class)
    fun provideBluetoothScanner(context: Context): BleScanner {
        return BleScanner(context)
    }
}
