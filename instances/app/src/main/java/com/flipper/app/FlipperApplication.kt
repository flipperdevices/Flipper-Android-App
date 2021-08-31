package com.flipper.app

import android.app.Application
import com.flipper.app.di.DaggerAppComponent
import com.flipper.core.di.ComponentHolder
import timber.log.Timber

class FlipperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ComponentHolder.components += DaggerAppComponent.factory()
            .create(this)

        if (BuildConfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.v("Started!")
    }
}
