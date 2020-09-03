package com.flipper.app.di

import com.flipper.app.home.di.HomeScreenDependencies
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent : HomeScreenDependencies
