package com.flipperdevices.bridge.connection

import android.app.Application
import com.flipperdevices.bridge.connection.di.AppComponent
import com.flipperdevices.bridge.connection.di.DaggerMergedAndroidAppComponent
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class ConnectionTestApplication : Application() {
    private val applicationScope = CoroutineScope(
        SupervisorJob() + FlipperDispatchers.workStealingDispatcher
    )

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerMergedAndroidAppComponent.factory()
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

        CurrentActivityHolder.register(this)

        applicationScope.launch {
            appComponent.rootLiveTest.awaitDeviceAndRunAll()
        }
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}
