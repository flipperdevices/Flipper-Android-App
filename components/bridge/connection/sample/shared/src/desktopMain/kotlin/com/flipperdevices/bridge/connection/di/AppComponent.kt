package com.flipperdevices.bridge.connection.di

import com.flipperdevices.bridge.connection.screens.ConnectionRootDecomposeComponent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

interface AppComponent {
    val themeViewModelProvider: Provider<ThemeViewModel>
    val rootComponentFactory: ConnectionRootDecomposeComponent.Factory
}