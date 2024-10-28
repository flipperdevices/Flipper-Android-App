package com.flipperdevices.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.flipperdevices.app.di.DaggerMergedAppComponent
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.singleactivity.impl.SingleActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class FlipperApplication : Application(), SingletonImageLoader.Factory, LogTagProvider {
    override val TAG = "FlipperApplication"

    private val applicationScope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)
    override fun onCreate() {
        super.onCreate()

        CurrentActivityHolder.register(this)

        val appComponent = DaggerMergedAppComponent.factory()
            .create(
                context = this,
                application = this,
                scope = applicationScope,
                ApplicationParams(
                    startApplicationClass = SingleActivity::class,
                    version = BuildConfig.VERSION_NAME
                )
            )

        ComponentHolder.components += appComponent

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
        try {
            val notificationApi by component.notificationApi
            notificationApi.init()
        } catch (e: Exception) {
            error(e) { "Failed init notification api" }
        }
        component.permissionRequestHandlerImpl.get().register(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context.applicationContext)
            .components {
                add(SvgDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }
}
