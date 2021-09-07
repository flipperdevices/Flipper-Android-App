package com.flipper.app.di

import com.flipper.app.MainActivity
import com.flipper.app.SplashScreen
import com.flipper.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface MainComponent {
    fun inject(activity: MainActivity)
    fun inject(splashScreen: SplashScreen)
}
