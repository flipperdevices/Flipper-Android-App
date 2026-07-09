package com.flipperdevices.wearable

import android.app.Application
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.wearable.di.AppComponent
import dev.zacsweers.metro.createGraphFactory
import com.flipperdevices.wearable.di.WearableComponent
import timber.log.Timber

class FlipperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CurrentActivityHolder.register(this)

        val appComponent = createGraphFactory<AppComponent.Factory>()
            .create(
                context = this,
                application = this,
                ApplicationParams(
                    startApplicationClass = MainWearActivity::class,
                    version = BuildKonfig.VERSION_NAME
                )
            )

        ComponentHolder.components += appComponent

        if (BuildKonfig.INTERNAL) {
            Timber.plant(Timber.DebugTree())
            val shake2report = ComponentHolder.component<WearableComponent>().shake2report.invoke()
            shake2report.init()
        }
    }
}
