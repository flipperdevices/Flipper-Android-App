package com.flipperdevices.core.navigation

import com.flipperdevices.core.di.AppGraph
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppGraph::class)
class NavigationModule {
    private val cicerone = Cicerone.create()

    @Provides
    fun provideRouter(): Router = cicerone.router

    @Provides
    fun provideNavigationHolder(): NavigatorHolder = cicerone.getNavigatorHolder()
}
