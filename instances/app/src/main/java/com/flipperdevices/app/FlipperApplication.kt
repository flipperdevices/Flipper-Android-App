package com.flipperdevices.app

import android.app.Application
import com.flipperdevices.analytics.shake2report.Shake2ReportApi
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
            Shake2ReportApi.init(this)
        }

        Timber.i("Start Flipper Application with version ${BuildConfig.VERSION_NAME}")
    }
}
