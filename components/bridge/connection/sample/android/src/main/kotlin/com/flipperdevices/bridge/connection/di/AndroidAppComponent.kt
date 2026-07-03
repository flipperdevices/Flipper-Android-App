package com.flipperdevices.bridge.connection.di

import android.app.Application
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@SingleIn(AppGraph::class)
@DependencyGraph(AppGraph::class)
interface AndroidAppComponent : AppComponent {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides context: Context,
            @Provides application: Application,
            @Provides applicationParams: ApplicationParams
        ): AndroidAppComponent
    }
}
