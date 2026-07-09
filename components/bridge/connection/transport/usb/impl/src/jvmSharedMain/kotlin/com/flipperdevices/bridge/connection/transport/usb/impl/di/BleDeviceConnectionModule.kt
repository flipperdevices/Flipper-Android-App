package com.flipperdevices.bridge.connection.transport.usb.impl.di

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.di.DeviceConnectionApiHolder
import com.flipperdevices.bridge.connection.transport.common.api.di.toHolder
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.usb.impl.USBDeviceConnectionApiImpl
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap

@ContributesTo(AppGraph::class)
interface BleDeviceConnectionModule {

    @Provides
    @IntoMap
    @ClassKey(FUSBDeviceConnectionConfig::class)
    fun provideBleDeviceConnectionApi(
        actionNotifierFactory: FlipperActionNotifier.Factory
    ): DeviceConnectionApiHolder = USBDeviceConnectionApiImpl(actionNotifierFactory).toHolder()
}
