package com.flipperdevices.app

import android.app.Application
import com.flipperdevices.app.di.DaggerAppComponent
import com.flipperdevices.core.di.ComponentHolder
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
