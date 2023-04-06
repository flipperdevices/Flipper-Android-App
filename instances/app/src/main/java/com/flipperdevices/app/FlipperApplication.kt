package com.flipperdevices.app

import android.app.Application
import com.flipperdevices.app.di.DaggerAppComponent
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.info
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.singleactivity.impl.SingleActivity
import tangle.inject.TangleGraph
import timber.log.Timber

class FlipperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CurrentActivityHolder.register(this)

        val appComponent = DaggerAppComponent.factory()
            .create(
                context = this,
                application = this,
                ApplicationParams(
                    startApplicationClass = SingleActivity::class,
                    version = "1.4.1.860",
                    isGooglePlayEnable = ApplicationParams.getIsGooglePlayEnableByProps(),
                )
            )

        ComponentHolder.components += appComponent
        TangleGraph.add(appComponent)

        if (BuildConfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
            val shake2report = ComponentHolder.component<MainComponent>().shake2report.get()
            shake2report.init()
        }
        setUp()

        info { "Start Flipper Application with version ${BuildConfig.VERSION_NAME}" }
    }

    private fun setUp() {
        val component = ComponentHolder.component<MainComponent>()
        val metricApi by component.metricApi
        metricApi.reportSimpleEvent(SimpleEvent.APP_OPEN)
        val synchronizationApi by component.synchronizationApi
        synchronizationApi.startSynchronization()
    }
}
