package com.flipperdevices.bridge.connection

import android.app.Application
import com.flipperdevices.bridge.connection.di.AppComponent
import com.flipperdevices.bridge.connection.di.DaggerAppComponent
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import timber.log.Timber

class ConnectionTestApplication : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory()
            .create(
                context = this,
                application = this,
                ApplicationParams(
                    startApplicationClass = ConnectionTestActivity::class,
                    version = "Sample"
                )
            )

        ComponentHolder.components += appComponent

        Timber.plant(Timber.DebugTree())
    }
}
