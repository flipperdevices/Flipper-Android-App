package com.flipperdevices.app.di

import android.app.Application
import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.SingleIn

/**
 * This component is meta-component.
 * In this file we merge all component which define with @ContributeTo(AppGraph::class)
 * So you can just create component with this annotation and then you can use it
 */
// Use singleton by default
@SingleIn(AppGraph::class)
@DependencyGraph(AppGraph::class)
interface AppComponent {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides context: Context,
            @Provides application: Application,
            @Provides scope: CoroutineScope,
            @Provides applicationParams: ApplicationParams
        ): AppComponent
    }
}
