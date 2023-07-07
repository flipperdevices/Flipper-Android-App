package com.flipperdevices.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.flipperdevices.app.di.DaggerAppComponent
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.info
import com.flipperdevices.singleactivity.impl.SingleActivity
import tangle.inject.TangleGraph
import timber.log.Timber

class FlipperApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        CurrentActivityHolder.register(this)

        val appComponent = DaggerAppComponent.factory()
            .create(
                context = this,
                application = this,
                ApplicationParams(
                    startApplicationClass = SingleActivity::class,
                    version = BuildConfig.VERSION_NAME
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
        val synchronizationApi by component.synchronizationApi
        synchronizationApi.startSynchronization()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }
}
