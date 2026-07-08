package com.flipperdevices.bridge.connection.transport.usb.impl.di

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.di.DeviceConnectionApiHolder
import com.flipperdevices.bridge.connection.transport.common.api.di.toHolder
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.usb.impl.USBDeviceConnectionApiImpl
import com.flipperdevices.bridge.connection.transport.usb.impl.handshake.FlipperRpcHandshake
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDeviceFactory
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
@ContributesTo(AppGraph::class)
class USBDeviceConnectionModule {

    @Provides
    @IntoMap
    @ClassKey(FUSBDeviceConnectionConfig::class)
    fun provideUSBDeviceConnectionApi(
        actionNotifierFactory: FlipperActionNotifier.Factory,
        platformDeviceFactory: USBPlatformDeviceFactory
    ): DeviceConnectionApiHolder = USBDeviceConnectionApiImpl(
        actionNotifierFactory = actionNotifierFactory,
        usbPlatformDeviceFactory = platformDeviceFactory,
        rpcHandshake = FlipperRpcHandshake()
    ).toHolder()
}
