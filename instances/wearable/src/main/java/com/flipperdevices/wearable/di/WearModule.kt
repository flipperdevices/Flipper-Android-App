package com.flipperdevices.wearable.di

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.google.android.gms.wearable.Wearable
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
@ContributesTo(AppGraph::class)
class WearModule {
    @Provides
    @Reusable
    fun provideChannelClient(context: Context) = Wearable.getChannelClient(context)

    @Provides
    @Reusable
    fun provideCapabilityClient(context: Context) = Wearable.getCapabilityClient(context)
}
