package com.flipperdevices.wearable.emulate.impl.di

import android.content.Context
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainResponse
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
@ContributesTo(WearGraph::class)
class WearableCommandStreamModule {
    @Provides
    @Reusable
    fun provideChannelClient(context: Context) = Wearable.getChannelClient(context)

    @Provides
    @Reusable
    fun provideCapabilityClient(context: Context) = Wearable.getCapabilityClient(context)

    @Provides
    @SingleIn(WearGraph::class)
    fun provideCommandInputStream(
        channelClient: ChannelClient
    ) = WearableCommandInputStream<MainResponse>(channelClient, MainResponse::parseDelimitedFrom)

    @Provides
    @SingleIn(WearGraph::class)
    fun provideCommandOutputStream(
        channelClient: ChannelClient
    ) = WearableCommandOutputStream<MainRequest>(channelClient)
}
