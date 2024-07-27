package com.flipperdevices.wearable.di

import android.app.Application
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import javax.inject.Singleton

@Singleton
@MergeComponent(AppGraph::class)
interface AppComponent {
    @MergeComponent.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance application: Application,
            @BindsInstance applicationParams: ApplicationParams
        ): AppComponent
    }
}
