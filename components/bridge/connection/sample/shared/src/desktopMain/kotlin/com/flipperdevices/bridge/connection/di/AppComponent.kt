package com.flipperdevices.bridge.connection.di

import com.flipperdevices.bridge.connection.livetests.RootLiveTest
import com.flipperdevices.bridge.connection.screens.ConnectionRootDecomposeComponent
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel
import dev.zacsweers.metro.Provider

interface AppComponent {
    val themeViewModelProvider: Provider<ThemeViewModel>
    val rootComponentFactory: ConnectionRootDecomposeComponent.Factory
    val rootLiveTest: RootLiveTest
}
