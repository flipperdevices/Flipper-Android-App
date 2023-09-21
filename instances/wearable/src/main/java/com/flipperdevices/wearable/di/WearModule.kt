package com.flipperdevices.wearable.di

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
@ContributesTo(AppGraph::class)
class WearModule {
    @Provides
    @Reusable
    fun provideChannelClient(context: Context) = Wearable.getChannelClient(context)

    @Provides
    @Reusable
    fun provideCapabilityClient(context: Context) = Wearable.getCapabilityClient(context)

    @Provides
    @Singleton
    fun provideCommandInputStream(
        channelClient: ChannelClient
    ) = WearableCommandInputStream<Main.MainResponse>(channelClient, Main.MainResponse::parseDelimitedFrom)

    @Provides
    @Singleton
    fun provideCommandOutputStream(
        channelClient: ChannelClient
    ) = WearableCommandOutputStream<Main.MainRequest>(channelClient)
}
