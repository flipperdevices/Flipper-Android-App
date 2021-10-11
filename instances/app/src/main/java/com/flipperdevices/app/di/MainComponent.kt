package com.flipperdevices.app.di

import com.flipperdevices.app.SplashScreen
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface MainComponent {
    fun inject(splashScreen: SplashScreen)
}
