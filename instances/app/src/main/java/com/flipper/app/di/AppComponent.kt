package com.flipper.app.di

import android.content.Context
import com.flipper.core.di.AppGraph
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * This component is meta-component.
 * In this file we merge all component which define with @ContributeTo(AppGraph::class)
 * So you can just create component with this annotation and then you can use it
 */
// Use singleton by default
@Singleton
@MergeComponent(AppGraph::class)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
