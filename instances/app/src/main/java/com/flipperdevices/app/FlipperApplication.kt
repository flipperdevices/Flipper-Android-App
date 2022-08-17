package com.flipperdevices.app

import android.app.Application
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.flipperdevices.app.di.DaggerAppComponent
import com.flipperdevices.app.di.MainComponent
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import tangle.inject.TangleGraph
import timber.log.Timber

class FlipperApplication : Application(), LogTagProvider {
    override val TAG = "FlipperApplication"

    private var referrerClient: InstallReferrerClient? = null

    override fun onCreate() {
        super.onCreate()

        CurrentActivityHolder.register(this)

        val appComponent = DaggerAppComponent.factory()
            .create(
                context = this,
                application = this,
                ApplicationParams(
                    startApplicationClass = SplashScreen::class,
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

        info { "Start Flipper Application with version ${BuildConfig.VERSION_NAME}" }
        try {
            printInstallReferrer()
        } catch (e: Exception) {
            error(e) { "While initialize referrer" }
        }
    }

    private fun printInstallReferrer() {
        info { "Start request install referrer" }
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                info { "onInstallReferrerSetupFinished: $responseCode" }
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response = referrerClient?.installReferrer
                        info {
                            """
                                Install referrer details. 
                                Referrer: ${response?.installReferrer}.
                                Click timestamp: ${response?.referrerClickTimestampSeconds}.
                                Install begin: ${response?.installBeginTimestampSeconds}.
                                Google Play Param: ${response?.googlePlayInstantParam}.
                                Click timestamp: ${response?.referrerClickTimestampServerSeconds}.
                                Begin timestamp: ${response?.installBeginTimestampServerSeconds}.
                                Install version:${response?.installVersion}.
                            """.trimIndent()
                        }
                    }
                    else -> {}
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                info { "onInstallReferrerServiceDisconnected" }
            }
        })
    }
}
