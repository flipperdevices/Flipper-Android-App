package com.flipperdevices.bridge.connection.di

import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.SingleIn

@SingleIn(AppGraph::class)
@DependencyGraph(AppGraph::class)
interface DesktopAppComponent : AppComponent {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides scope: CoroutineScope
        ): DesktopAppComponent
    }
}
