package com.flipperdevices.app

import android.app.Application
import com.flipperdevices.app.di.DaggerAppComponent
import com.flipperdevices.core.di.ComponentHolder
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberIntegration
import timber.log.Timber

class FlipperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ComponentHolder.components += DaggerAppComponent.factory()
            .create(this)

        if (BuildConfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
            SentryAndroid.init(this) {
                SentryTimberIntegration()
            }
        }

        Timber.v("Started!")
    }
}
