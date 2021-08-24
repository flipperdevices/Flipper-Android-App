package com.flipper.app

import android.app.Application
import com.flipper.app.di.AppComponent
import com.flipper.app.di.DaggerAppComponent
import timber.log.Timber

class FlipperApplication : Application() {
    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.factory()
            .create(this)

        if (BuildConfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.v("Started!")
    }
}
