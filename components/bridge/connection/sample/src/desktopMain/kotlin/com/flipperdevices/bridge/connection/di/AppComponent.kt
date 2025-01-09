package com.flipperdevices.bridge.connection.di

import com.flipperdevices.bridge.connection.screens.ConnectionRootDecomposeComponent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import kotlinx.coroutines.CoroutineScope
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@MergeComponent(AppGraph::class)
interface AppComponent {
    val themeViewModelProvider: Provider<ThemeViewModel>
    val rootComponentFactory: ConnectionRootDecomposeComponent.Factory

    @MergeComponent.Factory
    interface Factory {
        fun create(
            @BindsInstance scope: CoroutineScope
        ): AppComponent
    }
}
