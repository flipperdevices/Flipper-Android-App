package com.flipperdevices.wearable.di

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppGraph::class)
interface WearModule {
    @Provides
    @SingleIn(AppGraph::class)
    fun provideChannelClient(context: Context): ChannelClient = Wearable.getChannelClient(context)

    @Provides
    @SingleIn(AppGraph::class)
    fun provideCapabilityClient(context: Context): CapabilityClient =
        Wearable.getCapabilityClient(context)

    @Provides
    @SingleIn(AppGraph::class)
    fun provideCommandInputStream(
        channelClient: ChannelClient
    ): WearableCommandInputStream<MainResponse> =
        WearableCommandInputStream(channelClient, MainResponse.ADAPTER)

    @Provides
    @SingleIn(AppGraph::class)
    fun provideCommandOutputStream(
        channelClient: ChannelClient
    ): WearableCommandOutputStream<MainRequest> =
        WearableCommandOutputStream(channelClient, MainRequest.ADAPTER)
}
