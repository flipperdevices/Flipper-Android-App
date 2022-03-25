package com.flipperdevices.app

import android.app.Application
import com.flipperdevices.app.di.DaggerAppComponent
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.info
import timber.log.Timber

class FlipperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CurrentActivityHolder.register(this)

        ComponentHolder.components += DaggerAppComponent.factory()
            .create(
                this,
                ApplicationParams(startApplicationClass = SplashScreen::class)
            )

        if (BuildConfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
            val shake2report = ComponentHolder.component<MainComponent>().shake2report.get()
            shake2report.init(this)
        }

        info { "Start Flipper Application with version ${BuildConfig.VERSION_NAME}" }
    }
}
