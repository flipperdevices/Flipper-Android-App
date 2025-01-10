package com.flipperdevices.bridge.connection.di

import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Singleton
@MergeComponent(AppGraph::class)
interface DesktopAppComponent : AppComponent {
    @MergeComponent.Factory
    interface Factory {
        fun create(
            @BindsInstance scope: CoroutineScope
        ): DesktopAppComponent
    }
}
