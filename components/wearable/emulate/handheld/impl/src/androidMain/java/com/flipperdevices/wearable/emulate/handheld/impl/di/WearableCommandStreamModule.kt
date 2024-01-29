package com.flipperdevices.wearable.emulate.handheld.impl.di

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
@ContributesTo(WearHandheldGraph::class)
class WearableCommandStreamModule {
    @Provides
    @Reusable
    fun provideChannelClient(context: Context) = Wearable.getChannelClient(context)

    @Provides
    @SingleIn(WearHandheldGraph::class)
    fun provideCommandInputStream(
        channelClient: ChannelClient
    ) = WearableCommandInputStream<MainRequest>(channelClient, MainRequest::parseDelimitedFrom)

    @Provides
    @SingleIn(WearHandheldGraph::class)
    fun provideCommandOutputStream(
        channelClient: ChannelClient
    ) = WearableCommandOutputStream<MainResponse>(channelClient)
}
