package com.flipperdevices.nfceditor.sample

import android.app.Application
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.nfceditor.sample.di.AppComponent
import com.flipperdevices.nfceditor.sample.di.DaggerAppComponent
import timber.log.Timber

class NfcEditorApplication : Application() {
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
                    startApplicationClass = NfcEditorActivity::class,
                    version = "Sample"
                )
            )

        Timber.plant(Timber.DebugTree())
        val shake2report = appComponent.shake2report.get()
        shake2report.init()
    }
}
