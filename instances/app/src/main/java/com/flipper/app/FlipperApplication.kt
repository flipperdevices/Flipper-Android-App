package com.flipper.app

import android.app.Application
import com.flipper.app.di.ApplicationComponent
import com.flipper.app.di.ApplicationModule
import com.flipper.app.di.DaggerApplicationComponent
import timber.log.Timber

class FlipperApplication : Application() {
    companion object {
        lateinit var component: ApplicationComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()

        if (BuildConfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.v("Started!")
    }
}
